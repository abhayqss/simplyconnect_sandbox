"use strict";
// const debug = require('./debug');
const request = require('request');
const path = require('path');

module.exports = class pnHttpRequest{
	constructor(){
		this.options = {
			baseUrl: process.env.PNs_ADDRESS,
			uri: 'api/pushnotification',
			method: 'POST',
			headers: {
				'Content-Type'	: 'application/x-www-form-urlencoded',
				'Accept'		: 'application/json',
			},
			form: {},
			json: true,
		};
	}

	Push(pnTokens=null,alert=null,payload=null){
		return new Promise((Resolve,Reject)=>{
			if( !pnTokens  && !alert  && !payload ) return Reject('ALL arguments are required');

			this.options.uri = 'api/aps';
			this.options.form = {
				deviceToken	: pnTokens.join(),
				alert		: alert,
				payload		: payload,
			};

			request(this.options,(Err,Res,Body)=>{
				if(Err && !Body) return Reject(Err);

				let { data=null } = Body;
				return ( data ) ? Resolve(data.result) : Reject(Body);
			});
		});
	}
}
module.exports = class HttpRequest{
	constructor(){
		this.options = {
			baseUrl: process.env.PNs_ADDRESS,
			uri: '',
			method: 'POST',
			headers: {
				'Content-Type'	: 'application/x-www-form-urlencoded',
				'X-Auth-Token'	: ''
			},
			form: {},
			json: true
		};
	}

	PushRequest(pnTokens=null,alert=null,payload=null, params=null){
		return new Promise((Resolve, Reject)=>{			
			if( !pnTokens  && !alert  && !payload ) return Reject('ALL arguments are required');	
			
			if(params['phrSenderId']){
				payload['phrSenderId'] = params['phrSenderId'];
				payload['show_in_foreground'] = true;
			}
			
			if(params['phrReceiverId']){
				payload['phrReceiverId'] = params['phrReceiverId'];
			}		
			payload.text = payload.text ? Buffer.from(payload.text, 'utf8') : payload.text;
			var queryParams = "title=New Message Received&text="+payload.sender['name']+" has sent you a new message.&chatPayload="+JSON.stringify(payload);			
			this.options.uri = '/phr/'+ params['phrSenderId'] +'/notifications/sendpushtochat/'+params['phrReceiverId'].toString()+'?'+queryParams;			
			this.options.headers['X-Auth-Token'] = params['phrAuthToken'];
			
			request(this.options, (Err, Res, Body)=>{
				if(Err && !Body) return Reject(Err);
				
				return Resolve({'sent' : ['success']});
			})
		})
	}
} 
