const winston = require('winston')
const config = require('./src/config')

const level = config.log.level || 'info'

const transports = [];

transports.push(new (winston.transports.Console)({ level: level, colorize: true }))

if (config.log.output === 'file') {
	transports.push(new (winston.transports.File)({ filename: 'app.log', level: level }))
}

module.exports = winston.createLogger({
	exitOnError: false,
	transports: transports
})