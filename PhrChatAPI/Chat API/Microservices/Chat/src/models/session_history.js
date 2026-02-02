'use strict';
module.exports = function(sequelize, DataTypes) {
	var session_history = sequelize.define('session_history', {
		id: {
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.BIGINT.UNSIGNED
		},
		ip: {
			type: DataTypes.STRING,
		},
		inTime : {
			allowNull: false,
			type: DataTypes.DATE,
			defaultValue: DataTypes.NOW
		},
		outTime: {
			allowNull: true,
			type: DataTypes.DATE,
			defaultValue: null
		},
		user_id: {
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
		company_id: {
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
		handset_id: {
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
	}, {
		timestamps:false,
		classMethods: {
			associate: function(models) {
				// Belongs
				session_history.belongsTo(models.company, {foreignKey: 'company_id'});
				session_history.belongsTo(models.user, {foreignKey: 'user_id'});
				session_history.belongsTo(models.handset, {foreignKey: 'handset_id'});
			}
		}
	});
	return session_history;
};
