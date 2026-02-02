'use strict';
module.exports = function(sequelize, DataTypes) {
	var thread_participant = sequelize.define('thread_participant', {
		thread_id: {
			primaryKey: true,
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
		user_id: {
			primaryKey: true,
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
		enabled: {
			allowNull: false,
			type: DataTypes.BOOLEAN,
			defaulValue: false,
		}
	}, {
		timestamps: false,
		indexes: [{
			unique: false,
			fields: ['thread_id','user_id'],
		}],
		classMethods: {
			associate: function(models) {
				// Belongs
				thread_participant.belongsTo(models.thread, {foreignKey: 'thread_id'});
				thread_participant.belongsTo(models.user, {foreignKey: 'user_id'});
			}
		}
	});
	return thread_participant;
};
