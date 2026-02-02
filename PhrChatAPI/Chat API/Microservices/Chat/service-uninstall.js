var Service = require('node-windows').Service;

var svc = new Service({
	name:'Notify Chat micro service',
	script: require('path').join(__dirname,'/src/service.js')
});

svc.on('uninstall',function(){
	console.log('Uninstall complete.');
	console.log('The service exists: ',svc.exists);
});

svc.uninstall();
