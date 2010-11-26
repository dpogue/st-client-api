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
import cgi
try:
    from configparser import ConfigParser
except ImportError:
    from ConfigParser import ConfigParser

config = ConfigParser()
config.read(['webhook.conf'])

location = ''

class WebHookHandler(BaseHTTPRequestHandler):
    def do_POST(self):
        print('POST')
        try:
            fs = cgi.FieldStorage(
                    fp = self.rfile,
                    headers = self.headers,
                    environ = {
                        'REQUEST_METHOD': 'POST',
                        'CONTENT_TYPE': self.headers['Content-Type']
                    })

            if 'json_payload' not in fs:
                print('*** Error: No JSON payload with request!')
                self.send_error(500)
            print('%s' % fs['json_payload'].value)
        except:
            print('Problem reading data')
            self.send_error(500)

    def do_GET(self):
        self.send_error(404)

def register_hook():
    global location
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

    host = config.get('Webhook', 'Host')
    port = config.getint('Webhook', 'Port')
    secure = config.getboolean('Webhook', 'Secure')

    if not port is 80:
        host += ':%d' % port

    if secure:
        host = 'https://' + host
    else:
        host = 'http://' + host

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
    location = resp['location']
    print('*** Registered webhook at %s' % location)

def unregister_hook():
    global location
    username = config.get('Socialtext', 'Username')
    password = config.get('Socialtext', 'Password')
    server = config.get('Socialtext', 'Host')
    secure = config.getboolean('Socialtext', 'Secure')

    if secure:
        server = 'https://' + server
    else:
        server = 'http://' + server

    h = httplib2.Http()
    h.add_credentials(username, password)


    resp, content = h.request('%s%s' % (server, location), 'DELETE')
    print('*** Unregistered webhook')

    location = ''

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
    unregister_hook()

if __name__== '__main__':
    run_server()
