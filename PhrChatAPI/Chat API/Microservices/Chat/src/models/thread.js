'use strict';
module.exports = function(sequelize, DataTypes) {
	var thread = sequelize.define('thread', {
		id: {
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.BIGINT.UNSIGNED
		},
		name: {
			type: DataTypes.STRING(45)
		},
		description: {
			type: DataTypes.STRING
		},
		quantity: {
			allowNull: false,
			type: DataTypes.INTEGER.UNSIGNED
		}
	}, {
		classMethods: {
			associate: function(models) {
				// Has Many
				thread.hasMany(models.thread_message, {foreignKey: 'receiver'});
				thread.hasMany(models.thread_participant, {foreignKey: 'thread_id'});

				// Belongs to Many
				thread.belongsToMany(models.user, { as: 'thread_participants', through: models.thread_participant, foreignKey: 'thread_id' });
			}
		}
	});
	return thread;
};
