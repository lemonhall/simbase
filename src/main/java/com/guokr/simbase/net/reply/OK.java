package com.guokr.simbase.net.reply;

import org.wahlque.net.transport.Reply;
import org.wahlque.net.transport.payload.Status;

public class OK extends Status implements Reply<String> {

	public OK() {
		super("OK");
	}

}
