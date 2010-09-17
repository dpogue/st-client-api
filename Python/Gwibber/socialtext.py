
"""

SocialText interface for Gwibber
Darryl Pogue - 15/09/2010

"""

import network, util
from util import log
import urllib2
import json
from time import *
log.logger.name = 'SocialText'

PROTOCOL_INFO = {
  'name': 'SocialText',
  'version': 0.1,
  
  'config': [
      'username',
      'private:password',
      'receive_enabled',
      'send_enabled',
      'color'
  ],

  'color': '#E8F8FC',

  'features': [
      'receive',
      'send'
  ],
  
  'default_streams': [
    'receive',
  ],
}

RESTFUL_URL = 'https://developers.socialtext.net/'

class Client:
  def __init__(self, acct):
    self.account = acct
    self.timestamp = 0

  def __call__(self, opname, **args):
    return getattr(self, opname)(**args)

  def _message(self, data):
    log.logger.info('socialtext._message: ' + str(data))
    m = {}
    m['mid'] = str(data['signal_id'])
    m['service'] = 'socialtext'
    m['account'] = self.account['id']
    m['time'] = util.parsetime(data['at'])

    m['sender'] = {}
    m['sender']['nick'] = data['best_full_name']
    m['sender']['image'] = "%sdata/people/%d/photo" % (RESTFUL_URL, int(data['user_id']))
    m['sender']['url'] = "%s/st/profile/%d" % (RESTFUL_URL, int(data['user_id']))

    m['url'] = "%s/st/profile/%d" % (RESTFUL_URL, int(data['user_id']))

    m['content'] = data['body']
    m['text'] = data['body']

    return m

  def _reply(self, data):
    log.logger.info('socialtext._reply: ' + str(data))
    c = {}
    c['mid'] = str(data['signal_id'])
    c['service'] = 'socialtext'
    c['account'] = self.account['id']
    c['time'] = util.parsetime(data['at'])

    c['reply'] = {}
    c['reply']['id'] = data['in_reply_to']['signal_id']
    c['reply']['url'] = "%s%s" % (RESTFUL_URL, data['in_reply_to']['uri'])

    c['sender'] = {}
    c['sender']['nick'] = data['best_full_name']
    c['sender']['name'] = data['best_full_name']
    c['sender']['image'] = "%sdata/people/%d/photo" % (RESTFUL_URL, int(data['user_id']))
    c['sender']['url'] = "%s/st/profile/%d" % (RESTFUL_URL, int(data['user_id']))

    c['url'] = "%s%s" % (RESTFUL_URL, data['uri'])

    c['content'] = data['body']
    c['text'] = data['body']

    return c

  def receive(self):
    data = network.Download(RESTFUL_URL + 'data/signals', None, False,
            self.account["username"], self.account["password"],
            ['Content-Type: application/json', 'Accept: application/json']).get_json()

    messages = []
    for signal in data:
        if signal.has_key('in_reply_to'):
            c = self._reply(signal)
            log.logger.info('socialtext.receive: ' + str(c))
            messages.append(c)
        else:
            m = self._message(signal)
            log.logger.info('socialtext.receive: ' + str(m))
            messages.append(m)

    return messages

  def send(self, message):
    return []
