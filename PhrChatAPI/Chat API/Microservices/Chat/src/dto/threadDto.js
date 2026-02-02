module.exports = class threadDto{
	constructor(Thread,requesterId,extraProps=false){
		let {id,name,description,quantity,unreadMessages=0,lastMessage={}} = Thread;
		this.id					= id;
		this.name				= this.setName(Thread,requesterId);
		this.receiverId			= this.setReceiverId(Thread, requesterId);
		this.notifyUserId		= this.setNotifyUserId(Thread, requesterId);
		this.description		= description;
		this.quantity			= quantity;

		if( extraProps ) this.parseExtraProps(extraProps);
	}

	setName(Thread,requesterId){
		if( Thread.name ) return Thread.name;

		return Thread.thread_participants
			.map( p => (requesterId  != p.id) ? p.name : null )
			.filter( v => (v) )
			.join(', ');
	}

	parseExtraProps(props){
		if( typeof props == 'object' ){
			for (let k in props) {
				this[k] = props[k];
			}
		}
	}

	setReceiverId(Thread, senderUserId){
		var receiverId;
		Thread.thread_participants.forEach( p => {
			if(parseInt(p.thread_participant.user_id) != parseInt(senderUserId)){
				receiverId = p.thread_participant.user_id;
			}
		})

		return receiverId;
	}
	setNotifyUserId(Thread, senderUserId){
		var receiverId;
		Thread.thread_participants.forEach( p => {
			if(parseInt(p.thread_participant.user_id) != parseInt(senderUserId)){
				receiverId = p.notifyUserId;
			}
		})

		return receiverId;
	}
	setNotifyUserId(Thread, senderUserId){
		var receiverId;
		Thread.thread_participants.forEach( p => {
			if(parseInt(p.thread_participant.user_id) != parseInt(senderUserId)){
				receiverId = p.notifyUserId;
			}
		})

		return receiverId;
	}
}
