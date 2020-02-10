package it.unibo.finaltask.problemanalysis.test.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class QActorInterface {
	private final Socket socket;
	private final PrintWriter out;

	public QActorInterface(final String host, final int port) throws IOException {
		socket = new Socket(host, port);
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	public void sendMessage(final String command, final String msgType, final String from, final String to,
			final String payload, final String id) {
		sendMessage("msg(" + command + "," + msgType + "," + from + "," + to + "," + payload + "," + id + ")");
	}

	public void sendMessage(final String msg) {
		out.println(msg);
		out.flush();
	}

	public void close() throws IOException {
		out.close();
		socket.close();
	}
}
