package it.unibo.finaltask.project.test.utils;

import java.util.concurrent.CountDownLatch;
import java.util.function.Predicate;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;

public class CoapUtils {
	
	public static String getResourceValue(String uri) {
		CoapClient client = new CoapClient(uri);
		client.setTimeout(1000L);
		return client.get().getResponseText();
	}
	
	public static String pollResourceValue(final String uri, final Predicate<String> successCond) throws Exception {
	     CountDownLatch startSignal = new CountDownLatch(1);

		CoapClient client = new CoapClient(uri);
		client.setTimeout(1000L);

		if(!successCond.test(client.get().getResponseText())) {
			client.observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					String content = response.getResponseText();
					if(successCond.test(content)) {
						startSignal.countDown();
					}
				}
				@Override
				public void onError() { }
			});
			
			startSignal.await();
		}
		
		return client.get().getResponseText();
	}
}
