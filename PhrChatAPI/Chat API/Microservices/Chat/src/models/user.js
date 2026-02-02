'use strict';
module.exports = function(sequelize, DataTypes) {
	var user = sequelize.define('user', {
		id: {
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.BIGINT.UNSIGNED
		},
		notifyUserId: {
			allowNull: false,
			type: DataTypes.INTEGER.UNSIGNED
		},
		name:{
			allowNull: false,
			type: DataTypes.STRING
		},
		logged:{
			type: DataTypes.BOOLEAN,
			defaulValue: false,
		},
		role: {
			allowNull: false,
			type: DataTypes.ENUM('user','admin'),
			defaultValue: 'user',
		},
		current_handset: {
			allowNull: true,
			unique: true,
			type: DataTypes.STRING(38),
			set(val){
				let value = (val) ? val.toUpperCase() : null;
				this.setDataValue( 'current_handset' , value );
			},
		},
		company_id: {
			allowNull: false,
			type: DataTypes.BIGINT.UNSIGNED,

		},
		timezone_id: {
			type: DataTypes.INTEGER.UNSIGNED,
			allowNull: false,
		}
	}, {
		classMethods: {
			associate: function(models) {
				// Belongs
				user.belongsTo(models.company, {foreignKey: 'company_id'});
				user.belongsTo(models.timezone, {foreignKey: 'timezone_id'});
				user.belongsTo(models.handset, {foreignKey: 'current_handset',targetKey: 'uuid'});

				// Has
				user.hasMany(models.session_history, {foreignKey: 'user_id'});
				user.hasMany(models.thread_message, {foreignKey: 'sender'});
				// user.hasMany(models.thread_participant, {foreignKey: 'user_id'});

				// Belongs to Many
				user.belongsToMany(models.thread, { as: 'threads', through: models.thread_participant, foreignKey: 'user_id' });
			}
		}
	});
	return user;
};
