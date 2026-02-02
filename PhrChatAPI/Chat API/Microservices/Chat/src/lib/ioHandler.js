"use strict";
const SocketIO				= require('socket.io');
const JWT					= require('jsonwebtoken');
const Models				= require('../models');
const SecretKey				= process.env.SECRET;

var IO;
var liveNamespaces	= {};

class SocketEvents{
	constructor(ioServer){
		if( !(ioServer instanceof SocketIO) ){
			throw `Cannot proceed, constructor must receive a valid instance of Socket.io initialized param.`;
		}

		// bubble Initialized SocketIo into a higher scope
		IO = ioServer;
	}

	onConnection(incomingSocket){
		
		let Token = incomingSocket.handshake.query.token;

		return JWT.verify.call(this,Token,SecretKey,(err,decodedToken)=>{
			// refuse if invalid socket
			if(err){
				console.error('invalid Token!\n');
				incomingSocket.disconnect();
				return;
			}
			// just for DEVELOPMENT

			// Handler vars
			let namespace	= decodedToken.namespace;
			let userId		= decodedToken.user_id;
			let socketId	= incomingSocket.client.id;
			// Handler vars


			// disconnect if i'm not an Available User
			if( !(userId in liveNamespaces[namespace].availableUsers) ) return incomingSocket.disconnect();

			// Add a ref to this incoming Socket id for further events
			liveNamespaces[namespace].availableUsers[ userId ].rtHandler = incomingSocket;
			
			// add this user as online
			liveNamespaces[namespace].onlineUsers[ userId ] = liveNamespaces[namespace].availableUsers[ userId ].data;

			// Spread the word to all in the Namespace
			var list = this.getOnlineUsers(liveNamespaces);
			liveNamespaces[namespace].ioHandler.emit('userList',list);
			// liveNamespaces[namespace].ioHandler.emit('userList',this.getOnlineUsers(liveNamespaces));

			// Heartbeat Workaround
			let pongCount = 0;
			incomingSocket.on('pong',()=>{
				pongCount = 0;
			});

			let Heartbeat = setInterval(()=>{
				incomingSocket.emit('ping','ping');
				pongCount++;

				if( pongCount >= 100 ){
					//clearInterval(Heartbeat);
					//incomingSocket.disconnect();

					//delete liveNamespaces[namespace].onlineUsers[ userId ];

					// Spread the word to all in the Namespace
					//liveNamespaces[namespace].ioHandler.emit('userList',liveNamespaces[namespace].onlineUsers);
				}
			},3000);
			// Heartbeat Workaround

			// handle for inactive
			incomingSocket.on('disconnect',(data)=>{
				pongCount = 0;
				clearInterval(Heartbeat);
				delete liveNamespaces[namespace].onlineUsers[ userId ];

				// Spread the word to all in the Namespace
				var list = this.getOnlineUsers(liveNamespaces);
				liveNamespaces[namespace].ioHandler.emit('userList',list);
			});
		});
	}

	getOnlineUsers(liveNamespaces){
		var onlineUsers = {};
		for(let data in liveNamespaces){
			if(liveNamespaces[data].onlineUsers)
			{
				Object.assign(onlineUsers, liveNamespaces[data].onlineUsers);
			}
		}
		return onlineUsers;
	}
}

class namespaceHandler extends SocketEvents{
	constructor(param){
		super(param);
	}

	nspAdd(nsp){
		// Accept string Only
		if( !(typeof nsp  === 'string')) return console.error(`Cannot create RT chat with provided parameters:${nsp}`);
		if( nsp in liveNamespaces ) return console.error(`Cannot Add Namespace: "${nsp}" due already exists!`);


		let nspUri = `/${nsp}`;

		// liveNamespaces[ nsp ] = IO.of( nspUri );
		liveNamespaces[ nsp ] = {
			availableUsers 	: {},
			onlineUsers		: {},
			ioHandler		: IO.of( nspUri )
		};

		liveNamespaces[ nsp ].ioHandler.on('connection',this.onConnection.bind(this))

		return console.info(`Namespace: "${nspUri}" Enabled for RT chat`);
	}

	nspDel(){}

	addUser(nsp,user){
		
		if( !(typeof nsp  === 'string') || !(typeof user === 'object') ){
			return console.error(`	Cannot add user to namespace with provided parameters:${nsp},${user.id}-${user.name}`);
		}
		else if( !(nsp in liveNamespaces) ){
			return console.error(`	Cannot add user to Non-existent Namespace: "${nsp}"`);
		}
		else if( user.id in liveNamespaces[nsp].availableUsers ){
			return console.error(`	The user ${JSON.stringify(user)} was already available in ${nsp} Namespace`);
		}
		else{
			console.log(`	Added: "${user.name}" to RT available users on:	"${nsp}" namespace`);;

			var res = {
				"device_name" : user.handset,
				"id" : user.id,
				"name": user.name						
			}

			this.updateChanges(nsp, res);
			return liveNamespaces[nsp].availableUsers[user.id] = {
				data: {
					userId	: user.id,
					name	: user.name,
					handsetDetails : user.handset
				},
				rtHandler: false,
			};
		}
	}

	removeUser(nsp,user){

		if( !(typeof nsp  === 'string') || !(typeof user === 'object') ){
			return console.error(`	Cannot remove user from namespace with provided parameters:${nsp},${JSON.stringify(user)}`);
		}
		if( !(nsp in liveNamespaces) ){
			return console.error(`	Cannot remove user of Non-existent Namespace: "${nsp}"`);
		}
		if( !(user.id in liveNamespaces[nsp].availableUsers )){
			return console.error(`	The user "${user}" was already unavailable`);
		}

		// emit disconnection if that user was online
		if( liveNamespaces[nsp].availableUsers[user.id].rtHandler ){
			liveNamespaces[nsp].availableUsers[user.id].rtHandler.disconnect();
		}

		// Drop user from available and online users
		delete liveNamespaces[nsp].availableUsers[user.id];
		delete liveNamespaces[nsp].onlineUsers[user.id];

		var res = {
			"device_name" : '',
			"id" : user.id,
			"name": ""						
		}

		this.updateChanges(nsp, res);

		// spread the word about updated online users list
		liveNamespaces[nsp].ioHandler.emit('removeUser',liveNamespaces[nsp].onlineUsers);
	}

	updateDeliveryMessageTime(nsp,userId,payload){

		if( !(typeof nsp  === 'string') || !(typeof userId === 'number') || !(payload) ){
			console.error(`	Cannot add user to namespace provided parameters:${nsp},${userId},${payload}`);
			return false;
		}
		else if( !(nsp in liveNamespaces) ){
			console.error(`	Non-existent Namespace: "${nsp}"`);
			return false;
		}

		if(userId in liveNamespaces[nsp].onlineUsers){
			if(liveNamespaces[nsp].availableUsers[userId].rtHandler){					
				liveNamespaces[nsp].availableUsers[userId].rtHandler.emit('newDeliverMessage',payload);
													
				return true;
			}
		}
		
	}

	sendMessage(nsp,userId,payload){
		console.log('Flag 1');
		if( !(typeof nsp  === 'string') || !(typeof parseInt(userId) === 'number') || !(payload) ){
			console.error(`	Cannot add user to namespace provided parameters:${nsp},${userId},${payload}`);			
			return false;
		}
		else if( !(nsp in liveNamespaces) ){
			console.error(`	Non-existent Namespace: "${nsp}"`);			
			return false;
		}
		
		for(let data in liveNamespaces){
			if(liveNamespaces[data].availableUsers[userId])
			{
				liveNamespaces[data].availableUsers[userId].rtHandler.emit('newMessage',{"data": JSON.stringify(payload), "id": 6});
				return true;
			}
		}

		console.info(`	user:${userId} in Namespace:"${nsp}" is not active`);
		return false;
	}

	// Update immediately to app for any changes
	updateChanges(nsp, data){
		
		if( !(nsp in liveNamespaces)){
			console.error(`	Non-existent Namespace: "${nsp}"`);
			return false;
		}

		var res = {
			"device_name" : data['device_name'] ? data['device_name'] : '',
			"id" : data.id,
			"name" : data.name						
		}
		
		liveNamespaces[nsp].ioHandler.emit('broadcast', res);
	}

	// If handset is updated from Php Admin panel, update it to app
	updateHandset(nsp, handset){
		let wh = {
			where: { current_handset: handset['uuid']},
			attributes: ['id', 'name']
		}
		return Models.user.findAll(wh)
			.then((userInfo)=>{
				if(userInfo.length){

					var res = {
						"device_name" : handset['device_name']						
					}

					userInfo.forEach((data)=>{
						if(data.name){
							res['name'] = data.name;
						}
						if(data.id){
							res['id'] = data.id;
						}
					})

					this.updateChanges(nsp, res);
				}
			})
	}
}

module.exports = class ioHandler extends namespaceHandler{
	constructor(param){
		// Parent constructor call
		super(param);
		// initialize
		this.init();
	}

	init(){

		let Wh = {
			attributes: ['name','namespace'],
			where: { enabled: true },
			include: [{
				attributes: ['id','name','logged'],
				model: Models.user,
				//display device name of handset in mobile application
				include: [{
					attributes: ['device_name'],
					model: Models.handset
				}]
			}]
		};

		return Models.company.findAll(Wh)
		.then((Nsps)=>{

			if( !Nsps || Nsps.length < 1 ) return ;
			// inform about namespaces constuction
			console.info('\nEnabling RT chat for companies:\n');

			return Nsps.forEach((company)=>{

				// Build namespaces
				let actualNamespace = company.namespace;

				let nspUri = this.nspAdd( actualNamespace );

				// And then Add those users to Available users Arr
				company.users.forEach((user)=>{
					var userModel = {
						id: user.id,
						name: user.name,
						// Sending handset information to display on mobile application
						handset: user.handset
					}
					if(user.logged) this.addUser(actualNamespace,userModel);
				});
			});
		})
		.catch((Err)=>{ throw Err; });
	}
}
