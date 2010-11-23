#!/usr/bin/env python

from __future__ import print_function
import json

try:
    from http.server import BaseHTTPRequestHandler
    from http.server import HTTPServer
except ImportError:
    from BaseHTTPServer import BaseHTTPRequestHandler
    from BaseHTTPServer import HTTPServer

import httplib2
import configparser
#import SocketServer

config = configparser.ConfigParser()
config.read(['webhook.conf'])

class WebHookHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        print('POST')
        print(self.rfile.read())

    def do_GET(self):
        print('GET')
        print(self.rfile.read())

def register_hook():
    username = config.get('Socialtext', 'Username')
    password = config.get('Socialtext', 'Password')
    server = config.get('Socialtext', 'Host')
    reg_class = config.get('Socialtext', 'WebhookClass')
    group = config.getint('Socialtext', 'Group')
    secure = config.getboolean('Socialtext', 'Secure')

    if secure:
        server = 'https://' + server
    else:
        server = 'http://' + server

    print(server)

    host = config.get('Webhook', 'Host')
    port = config.getint('Webhook', 'Port')
    secure = config.getboolean('Webhook', 'Secure')

    if not port is 80:
        host += ':%d' % port

    if secure:
        host = 'https://' + host
    else:
        host = 'http://' + host

    print(host)

    h = httplib2.Http()
    h.add_credentials(username, password)

    fields = {
        'class': reg_class,
        'group_id': group,
        'url': host
    }

    resp, content = h.request('%s/data/webhooks' % server,
            'POST', body = json.dumps(fields),
            headers = { 'content-type': 'application/json'} )
    print(resp)

def run_server():
    server = config.get('Server', 'Host')
    port = config.getint('Server', 'Port')

    httpd = HTTPServer((server, port), WebHookHandler)
    register_hook()
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        pass
    httpd.server_close()

if __name__== '__main__':
    run_server()
