package itunibo.robotReal

import itunibo.robot.GrabberSupport
import itunibo.robot.ClassifierSupport
import java.io.InputStreamReader
import java.io.BufferedReader
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import java.io.InputStream
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity


class ConsoleGrabberSupport(frontEndUrl: String) :
	GrabberSupport,
	ClassifierSupport {

	var lastObstacle: String = "nothing"
	var handler: (String) -> Unit = { _ -> }
	val frontEndUrl: String

	init {

		println("ConsoleGrabberSupport started with $frontEndUrl")
		this.frontEndUrl = frontEndUrl
	}

	override fun grab(): Boolean {
		val result = lastObstacle.contains("bottle")
		lastObstacle = "nothing"
		return result
	}

	override fun classify(handler: (String) -> Unit) {

		try {

			val cmd = arrayOf(
				"/bin/sh",
				"-c",
				"raspistill -vf -hf -o - | base64 -w 0 ; echo -e \"\\n\""
			)
			val p = Runtime.getRuntime().exec(cmd);
			val input = p.getInputStream();
			val buffer = StringBuilder(3500000);
			var c: Char;
			while (true) {
				c = input.read().toChar();
				if (c != '\n') {
					buffer.append(c);
				} else {
					break;
				}
			}
			p.destroy();
			val img = buffer.toString();


			val httpclient = HttpClients.createDefault();
			val httppost = HttpPost(frontEndUrl);


			val json = "{\"img\":\"$img\"}";
			val entity = StringEntity(json);
			httppost.setEntity(entity);
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");

			val response = httpclient.execute(httppost);
			val code = response.getStatusLine().getStatusCode()
			println("Upload done: $code")


		} catch (ex: Exception) {
			println(ex)
		}

		this.handler = handler
	}

	override fun suggest(obstacle: String) {
		println("A suggestion was received: obstacle is " + obstacle)
		handler(lastObstacle)
		handler = { _ -> }
	}
}
