package com.guokr.simbase.action;

import java.nio.ByteBuffer;
import java.util.Map;

import org.wahlque.net.action.Action;
import org.wahlque.net.action.ActionException;
import org.wahlque.net.action.Command;
import org.wahlque.net.transport.Payload;
import org.wahlque.net.transport.payload.Bytes;
import org.wahlque.net.transport.payload.Multiple;

import com.guokr.simbase.SimBase;
import com.guokr.simbase.command.Update;
import com.guokr.simbase.reply.OK;

public class UpdateAction implements Action {

	public static final String ACTION = "vupdt";

	public Multiple payload(Map<String, Object> context, Command command)
			throws ActionException {

		Update cmd = (Update) command;

		Bytes[] value = new Bytes[cmd.pairs.length + 3];

		value[0] = new Bytes(ACTION.getBytes());

		value[1] = new Bytes(cmd.key.getBytes());

		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(cmd.docid);
		value[2] = new Bytes(bb.array());

		int i = 2;
		for (int idx = 0; idx < cmd.pairs.length;) {
			value[++i] = new Bytes(cmd.pairs[idx++].toString().getBytes());
			bb = ByteBuffer.allocate(4);
			bb.putFloat(Float.parseFloat(cmd.pairs[idx++].toString()));
			value[++i] = new Bytes(bb.array());
		}

		return new Multiple(value);
	}

	public Command command(Map<String, Object> context, Payload<?> payload)
			throws ActionException {

		Update cmd = new Update();

		Multiple multiple = (Multiple) payload;
		Payload<?>[] items = multiple.data();

		Bytes actionBytes = (Bytes) items[0];
		assert (new String(actionBytes.data()).equals(ACTION));

		Bytes keyBytes = (Bytes) items[1];
		cmd.key = new String(keyBytes.data());

		Bytes docidBytes = (Bytes) items[2];
		cmd.docid = Integer.parseInt(new String(docidBytes.data()));

		int size = items.length - 3;
		Object[] array = new Object[size];
		for (int i = 0; i < size;) {
			Bytes stringBytes = (Bytes) items[i + 3];
			array[i] = new String(stringBytes.data());
			Bytes floatBytes = (Bytes) items[i + 4];
			array[i + 1] = Float.parseFloat(new String(floatBytes.data()));
			i = i + 2;
		}
		cmd.pairs = array;

		return cmd;
	}

	public Payload<?> apply(Map<String, Object> context, Payload<?> data)
			throws ActionException {
		Update cmd = (Update) command(context, data);
		((SimBase) context.get("simbase"))
				.update(cmd.key, cmd.docid, cmd.pairs);
		return new OK();
	}

}
