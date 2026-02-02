"use strict";
// only for debugging
const debug		= require('../lib/debug');
const Models	= require('../models');
const threadDto	= require('../dto/threadDto');

// Notify other user online user via socket
function sendDeliveryInformation(nsp, iDs, senderID=false, params){


	let Val = { deliveredAt: new Date() };
		let Wh 	= {
			where: {
				id: iDs,
				deliveredAt : null,
			},
		};
	if( senderID ) Wh.where.sender = {$ne:senderID};

	Models.thread_message.findAll(Wh)
		.then((messages)=>{
				
			if(messages.length > 0){
		 		messages.map( data => {
					data.deliveredAt = new Date();
				})

				let thread_Wh = {
					where: {
							thread_id : params.id,	
					}
				};

				// Get receiver thread thread_participants except sender
				return Models.thread_participant.findAll(thread_Wh)
					.then((threadParticipants)=>{
						
						threadParticipants.forEach(threadParticipant => {
							global.ioHandler.updateDeliveryMessageTime(nsp,threadParticipant.user_id,messages);

						})
				})
			}
									
		})
}

// MessageUtilities
function markMessagesAsDelivered(iDs,senderID=false){

	let Val = { deliveredAt: new Date() };
	let Wh 	= {
		where: {
			id: iDs,
			deliveredAt : null,
		},
	};
	if( senderID ) Wh.where.sender = {$ne:senderID};



	return Models.thread_message.update(Val,Wh)
	.then( data => data )
	.catch( Err => Err );
}

/* Thread Controller Class */
module.exports = class controllerThread{
	constructor(){
	}

	create(params){
		return new Promise((Resolve,Reject)=>{

			let insertParticipants	= false;
			let {user_id}			= params;
			let { name, description, participants } = params;
			let insertThread = {
				name: name,
				description: description,
			};

			if( !params.participants ) return Reject('Required param missing: "participants"');

			if( params.participants && !(/^(\d+(,\d+)*)?$/.test(params.participants)) ){
				return Reject('participants must be comma separated id string');
			} else {
				// split and remove duplicated
				insertParticipants = participants.split(',').filter( (v,k,s) => k == s.indexOf(v) );
			}

			// config last field for insert Thread
			insertThread.quantity = insertParticipants.length;

			// error handle
			if( insertThread.quantity == 1 ){
				return Reject('"participants" a new thread must involve more than 1 participants');
			}
			// handler for creating 1-1 thread
			else if( insertThread.quantity == 2 ){
				let findThreadWh = {
					where: {
						quantity: 2,
					},
					include: [{
						as: 'thread_participants',
						attributes: ['id','name','current_handset'],
						model: Models.user,
					}],
				};

				return Models.thread.findAll(findThreadWh)
				.then((threadsWith2)=>{
					if( threadsWith2.length == 0 ){
						return this.createGroup(insertThread,insertParticipants,user_id)
						.then( data => Resolve(data) )
						.catch( Err => Reject(Err) );
					}

					for( let i = 0; i < threadsWith2.length; i++) {
						let [a,b] = insertParticipants;
						let [x,y] = threadsWith2[i].thread_participants;

						if( ( x && y ) && ((x.id == a &&  y.id == b) || (x.id == b &&  y.id == a)) ){
							return this.getOne({id:threadsWith2[i].id,user_id:user_id})
							.then( data => Resolve(data) )
							.catch( Err => Reject(Err) );
						}

						// if is the last loop and there is not any coincidence
						if( threadsWith2.length-i == 1 ){
							return this.createGroup(insertThread,insertParticipants,user_id)
							.then( data => Resolve(data) )
							.catch( Err => Reject(Err) );
						}
					}
				})
				.catch( Err => Reject(Err) );
			}
			// handler for creating groups
			else{
				return this.createGroup(insertThread,insertParticipants,user_id)
				.then( data => Resolve(data) )
				.catch( Err => Reject(Err) );
			}
		});
	}

	createGroup(Thread,insertParticipants,userId){
		return new Promise((Resolve,Reject)=>{

			return Models.thread.create(Thread)
			.then((newThread)=>{
				
				if( !insertParticipants ){
					newThread.destroy();
					return Reject('participants must be comma separated id string or Array');
				}

				// compose bulk_Create for thread_participants
				return {
					thread: newThread,
					thread_participants: insertParticipants.map((v)=>{
						return {
							thread_id: newThread.id,
							user_id: v,
						};
					}),
				};
			})
			.then((data)=>{
				// execute bulk_Create for thread_participants
				return Models.thread_participant.bulkCreate(data.thread_participants)
				.then( (participants) => data)
				.catch((Err)=>{
					data.thread.destroy();
					return Reject(Err);
				});

			})
			// Reserved for Additional Task
			.then((data)=>{
				let id = data.thread.id;
				return this.getOne({id:id,user_id:userId})
				.then( data => Resolve(data) )
				.catch( Err => Reject(Err) );
			})
			.catch( Err => Reject(Err) );
		});
	}

	getOne({id,user_id}){
		return new Promise((Resolve,Reject)=>{

			let Wh = {
				where: { id: id },
				include: [{
					as: 'thread_participants',
					attributes: ['id','notifyUserId','name','logged','company_id','timezone_id'],
					model: Models.user,
				}],
			};

			return Models.thread.findOne(Wh)
			.then((Thread)=>{
				if (!Thread) return Reject('Non-existent Thread');

				return new threadDto( Thread , user_id );
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getAll(params){
		return new Promise((Resolve,Reject)=>{

			let {user_id,company_id} = params;
			let Wh = {
				include: [{
					as: 'thread_participants',
					model: Models.user,
					attributes: ['notifyUserId','name','logged','company_id','timezone_id'],
					where: {company_id: company_id},
				}],
				limit: params.limit,
				offset: params.offset,
			};

			return Models.thread.findAndCountAll(Wh)
			.then((Threads)=>{
				if (!Threads) return Reject('Non-existent Threads');

				return {
					count	: Threads.count,
					rows	: Threads.rows.map( Thread => new threadDto( Thread , user_id ) ),
				};
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	edit(id,params){
		return new Promise((Resolve,Reject)=>{

			return Models.thread.findById(id)
			.then((oldThread)=>{
				if (!oldThread) return Reject('Non-existent Thread');

				if( params.name ){
					oldThread.name = params.name;
				}
				if( params.description ){
					oldThread.description = params.description;
				}

				return oldThread.save();
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	delete({id}){
		return new Promise((Resolve,Reject)=>{

			return Models.thread.findById(id)
			.then((condemnedThread)=>{
				if (!condemnedThread) return Reject('Non-existent Thread');

				return condemnedThread.destroy();
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}

	getMessages(params){
		return new Promise((Resolve,Reject)=>{

			let {id,user_id, namespace} = params;

			let thWh = {
				where: { id: id },
				include: [{
					as: 'thread_participants',
					attributes: ['id','notifyUserId','name','logged','company_id','timezone_id'],
					model: Models.user,
				}],
			};

			return Models.thread.findOne(thWh)
			.then((Thread)=>{
				if (!Thread) return Reject('Non-existent Thread');

				let userId;
				if(Thread.thread_participants.length > 0){
					Thread.thread_participants.forEach((participant)=>{
						if(parseInt(params.user_id) == parseInt(participant.id)){
							userId = participant.id;
						}
					})
				}
				
				let thPc = {
					where: { thread_id: params.id, user_id: userId}
				}

				return Models.thread_participant.findOne(thPc)
				.then((ThreadParticipant)=>{
					if(params.enabled == "true"){
						ThreadParticipant.enabled = true;
						ThreadParticipant.save();
					}
					else{
						ThreadParticipant.enabled = false;
						ThreadParticipant.save();
					}

					return  new threadDto( Thread , user_id );
				})						
			})
			.then((Thread)=>{
				if (!Thread) return Reject('Non-existent Thread');

				let msgWh = {
					where: {
						receiver: Thread.id,
					},
					limit: params.limit,
					offset: params.offset,
					order: [['createdAt', 'DESC']],
				};

				return Models.thread_message.findAndCountAll(msgWh)
				.then((messages)=>{		
					messages.rows.forEach((msg)=>{
						msg.text = msg.text ? msg.text.toString('utf8') : msg.text;
					})
					
					let fetchedMessages = messages.rows.map( v => v.id );

					// send delivery time to user
					sendDeliveryInformation(namespace, fetchedMessages ,user_id,  params );

					// update deliveredAt message Status
				if( fetchedMessages.length > 0 ) markMessagesAsDelivered( fetchedMessages , user_id );

					// Prepare data to respond
					Thread.thread_messages	= messages.rows;
					return {
						rows	: Thread,
						count	: messages.count,
					};
				})
				.catch( Err => Reject(Err) );
			})
			.then( data => Resolve(data) )
			.catch( Err => Reject(Err) );
		});
	}
};
