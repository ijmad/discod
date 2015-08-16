FROM gliderlabs/alpine:3.1

RUN apk --update add dnsmasq

RUN rm /etc/dnsmasq.conf
ADD dnsmasq.conf /etc/
ADD service etc/dnsmasq.d/service 

EXPOSE 53

ENTRYPOINT ["dnsmasq", "--no-daemon"]
