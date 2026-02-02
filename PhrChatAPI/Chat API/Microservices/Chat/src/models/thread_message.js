'use strict';
module.exports = function(sequelize, DataTypes) {
	var thread_message = sequelize.define('thread_message', {
		id: {
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.BIGINT.UNSIGNED
		},
		sender: {
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
		receiver: {
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
		type: {
			allowNull: false,
			type: DataTypes.ENUM('text','image','audio','video','stream'),
			defaultValue: 'text',
		},
		text: {
			allowNull: false,
			type: DataTypes.BLOB
		},
		createdAt: {
			allowNull: false,
			type: DataTypes.DATE,
			defaultValue: DataTypes.NOW,
		},
		notifiedAt: {
			allowNull: true,
			type: DataTypes.DATE,
		},
		deliveredAt: {
			allowNull: true,
			type: DataTypes.DATE
		}
	}, {
		timestamps:false,
		classMethods: {
			associate: function(models) {
				// Belongs
				thread_message.belongsTo(models.user, { foreignKey: 'sender'});
				thread_message.belongsTo(models.thread, {foreignKey: 'receiver'});
			}
		}
	});
	return thread_message;
};
