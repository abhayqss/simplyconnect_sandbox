var Service = require('node-windows').Service;

var svc = new Service({
	name:'Notify Chat micro service',
	description: 'Socket.io / Node.js application',
	script: require('path').join(__dirname,'/src/service.js'),
	abortOnError:true
});

svc.on('install',function(){
	console.log('Service installed.');
	svc.start();
});

svc.install();
