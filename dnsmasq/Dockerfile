FROM gliderlabs/alpine:3.1

RUN apk --update add bash
RUN apk --update add dnsmasq

RUN rm /etc/dnsmasq.conf
ADD dnsmasq.conf /etc/

WORKDIR /app
ADD dnsmasq.sh /app/

EXPOSE 53

CMD ./dnsmasq.sh
