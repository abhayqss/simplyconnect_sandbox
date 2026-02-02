'use strict';
const bcrypt = require('bcryptjs');

module.exports = function(sequelize, DataTypes) {
	var TenantMaster = sequelize.define('tenant_master', {
		id: {
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.INTEGER.UNSIGNED
		},
		username: {
			allowNull: false,
			unique: true,
			type: DataTypes.STRING
		},
		password: {
			allowNull: false,
			type: DataTypes.STRING(255),
			set(val){
				let { password, passwordConfirmation } = val;
				if( password !== passwordConfirmation ) throw 'Passwords does not Match';

				let hash = bcrypt.hashSync( password , bcrypt.genSaltSync(14) );

				this.setDataValue('password',hash);
			},
		}
	}, {
		classMethods: {
			associate: function(models) {
				// associations can be defined here
			}
		}
	});
	return TenantMaster;
};
