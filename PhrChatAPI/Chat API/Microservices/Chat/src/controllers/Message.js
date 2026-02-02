"use strict";
// only for debugging
// const debug				= require('../lib/debug');
const Models			= require('../models');
const pnHttpRequest		= require('../lib/pnHttpRequest');
const httpRequest		= require('../lib/pnHttpRequest');

function sendPushNotifications(pnTokens,alert,newMessage, params){
	let obj = new httpRequest;
	return obj.PushRequest(pnTokens,alert,newMessage, params)
	.then(({sent=[],failed=[]})=>{
		if( failed.length > 0 ){
			console.log(`notification not delivered : `);
			console.warn( failed );
		}

		if( sent.length = 0)	return newMessage;
		let Val = { notifiedAt: new Date() };
		let Wh 	= {
			where: { id: newMessage.id },
		};

		return Models.thread_message.update(Val,Wh)
		.then((data)=>{ return newMessage; })
		.catch( Err => Err );
	})
	.catch( Err => Err );
}

/* Message Controller Class */
module.exports = class controllerMessage{
	constructor(){
	}

	create(params){
		return new Promise((Resolve,Reject)=>{

			let companyId = params.company_id;
			let Message = {
				sender		: parseInt(params.sender),
				receiver	: parseInt(params.receiver),
				text		: Buffer.from(params.text, 'utf8'),
			};

			return Models.thread_message.create(Message)
			.then((newMessage)=>{
				if(!newMessage) return Reject('Error on message creation');

				return Models.user.findById( newMessage.sender )
				.then((User)=>{
					return {
						id: newMessage.id,
						sender: {
							id		: User.id,
							name	: User.name

						},
						receiver	: parseInt(newMessage.receiver),
						text		: newMessage.text ? newMessage.text.toString('utf8') : newMessage.text,
						createdAt 	: newMessage.createdAt,
						deliveredAt	: newMessage.deliveredAt
					};
				})
				.catch( Err => Reject(Err) );
			})
			.then((newMessage)=>{
				
				let Wh = {
					where: {
						id: newMessage.receiver,
					},
					include: [{
						as: 'thread_participants',
						attributes: ['id','name','current_handset','logged'],
						model: Models.user,
						where: {
							id: {
								$ne: parseInt(params.sender)
							}
						}
					}],
				};
				// Get receiver thread thread_participants except sender
				return Models.thread.findOne(Wh)
				.then((thread)=>{

				 	return {newMessage:newMessage,messageReceivers:thread.thread_participants}; 
				})
				.catch( Err => Reject(Err) );
			})
			.then((data)=>{

				let {newMessage,messageReceivers} = data;
				if(!(newMessage) || !(messageReceivers)) return Reject('Error Getting Thread thread_participants');

				// prepare data to continue while delivering RT messages
				if(newMessage){
					newMessage.phrSenderId = params.phrSenderId;
					newMessage.phrReceiverId = params.phrReceiverId;
					newMessage.show_in_foreground = true;
				}

				// For one-to-one chat
				let messageReceiver = messageReceivers[0];
				let nsp = params.namespace;
				let userId = messageReceiver.id;

				let rtDelivered = global.ioHandler.sendMessage(nsp,userId,newMessage);

				let obj = {
					newMessage: newMessage,
					pnReceivers: [null]
				};

				if(rtDelivered){
					let wh = {
						where: { thread_id : params.thread_id }
					}

					return Models.thread_participant.findAll(wh)
					.then((ThreadParticipant)=>{
						return ThreadParticipant;
					})
					.then((ThreadParticipant)=>{

						let userConnected = true;
						ThreadParticipant.forEach((participant)=>{
							if(!participant.enabled){
								userConnected = false;
							}
						})

						if(userConnected){
							return Models.thread_message.findById(newMessage.id)
							.then((message)=>{		
								message.deliveredAt = new Date();				
								message.save();
								return obj;
							})		
						}
						else{								
							obj.pnReceivers = [messageReceiver.current_handset];
							return obj;
						}
					})
				}
				else {
					obj.pnReceivers = [messageReceiver.current_handset];
					return obj;
				}

			})
			.then((data)=>{
				let {newMessage,pnReceivers} = data;
				if(!newMessage || !pnReceivers) return Reject('Error delivering RT messages');

				let hsWh = {
					attributes: ['pn_token'],
					where: {
						uuid: {
							$in: pnReceivers
						}
					}
				};

				// get hs PN Token
				return Models.handset.findAll(hsWh)
				.then((Handsets)=>{
					return {newMessage:newMessage,Handsets:Handsets};
				})
				.catch( Err => Reject(Err) );
			})
			.then((data)=>{
				let {newMessage,Handsets} = data;
				if(!newMessage || !Handsets) return Reject('Error Getting Handsets delivered RT messages');

				if(Handsets.length == 0 ){
					return {
						newMessage:newMessage,
						alert		:null,
						pnTokens	:null,
					};
				} else {
					// Prepare Params for Notification(s)
					return {
						newMessage	: newMessage,
						alert		: `${newMessage.sender.name} says: ${newMessage.text}`,
						pnTokens	: Handsets.map( v => v.pn_token ),
					};
				}
			})
			.then((data)=>{
				let {newMessage,pnTokens=null,alert=null} = data;

				if( (pnTokens) && pnTokens.length > 0 ) sendPushNotifications( pnTokens,alert,newMessage, params );
				return newMessage;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getOne({id}){
		return new Promise((Resolve,Reject)=>{

			return Models.thread_message.findById(id)
			.then((Message)=>{
				if (!Message) return Reject('Non-existent Message');

				return Message;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getAll(params){
		return new Promise((Resolve,Reject)=>{

			let Wh = {
				limit: params.limit,
				offset: params.offset,
			};

			return Models.thread_message.findAndCountAll(Wh)
			.then((Message)=>{
				if (!Message) return Reject('Non-existent Message');

				return Message;
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
	
	markMessageAsNotified(id){
		return Models.thread_message.findById(id)
		.then((message)=>{		
			message.deliveredAt = new Date();				
			return message.save();
		})		
	}

	edit(id,Message){
		return new Promise((Resolve,Reject)=>{

			return Models.thread_message.findById(id)
			.then((oldMessage)=>{
				if (!oldMessage) return Reject('Non-existent Message');

				if( Message.text ){
					oldMessage.text = Message.text;
				}

				oldMessage.delivered = ( Message.delivered );

				return oldMessage.save();
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	delete({id}){
		return new Promise((Resolve,Reject)=>{

			return Models.thread_message.findById(id)
			.then((condemnedMessage)=>{
				if (!condemnedMessage) return Reject('Non-existent Message');

				return condemnedMessage.destroy();
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
}
