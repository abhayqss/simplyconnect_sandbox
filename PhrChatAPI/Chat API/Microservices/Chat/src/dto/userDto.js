module.exports = class userDto{
	constructor({id,notifyUserId,name,logged,role,company_id,timezone_id,threads=[]}){
		this.id			= id;
		this.notifyUserId	= notifyUserId;
		this.name			= name;
		this.logged			= logged;
		this.role			= role;
		this.company_id		= company_id;
		this.timezone_id	= timezone_id;
		this.threads		= threads;
	}
}
