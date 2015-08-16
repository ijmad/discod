package com.ianjm.discod;

import org.xbill.DNS.ARecord;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Header;
import org.xbill.DNS.Message;
import org.xbill.DNS.Opcode;
import org.xbill.DNS.Rcode;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Optional;
import java.util.function.Function;

public class DNSResponder {
    private final Function<String, Optional<InetAddress>> handler;

    public DNSResponder(Function<String, Optional<InetAddress>> handler) {
        this.handler = handler;
    }

    public Message respond(Message query) throws IOException {
        Header header = query.getHeader();

        if (header.getFlag(Flags.QR)) {
            return null;
        }

        if (header.getRcode() != Rcode.NOERROR) {
            return errorMessage(query, Rcode.FORMERR);
        }

        if (header.getOpcode() != Opcode.QUERY) {
            return errorMessage(query, Rcode.NOTIMP);
        }

        Record question = query.getQuestion();
        Message response = new Message(query.getHeader().getID());

        response.getHeader().setFlag(Flags.QR);
        response.addRecord(question, Section.QUESTION);

        Optional<InetAddress> ip = handler.apply(question.getName().toString());
        if (ip.isPresent()) {
            response.getHeader().setRcode(Rcode.NOERROR);
            response.addRecord(new ARecord(question.getName(), question.getDClass(), 1l, ip.get()), Section.ANSWER);
        }
        else {
            response.getHeader().setRcode(Rcode.NXDOMAIN);
        }

        return response;
    }

    private static Message errorMessage(Message query, int rcode) {
        Message response = new Message();
        response.setHeader(query.getHeader());
        query.getHeader().setRcode(rcode);

        for (int i = 0; i < 4; i++) {
            response.removeAllRecords(i);
        }
        if (rcode == Rcode.SERVFAIL) {
            response.addRecord(query.getQuestion(), Section.QUESTION);
        }

        return response;
    }
}
