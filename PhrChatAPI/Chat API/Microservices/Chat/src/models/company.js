'use strict';
const bcrypt = require('bcryptjs');

module.exports = function(sequelize, DataTypes) {
	var company = sequelize.define('company', {
		id:{
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.BIGINT.UNSIGNED
		},
		notifyCompanyId: {
			allowNull: false,
			type: DataTypes.INTEGER.UNSIGNED
		},
		name: {
			allowNull: false,
			type: DataTypes.STRING
		},
		namespace:{
			unique: true,
			type: DataTypes.STRING(45),
			defaultValue: DataTypes.UUIDV4,
			set(val){
				if( !( /^[a-z-_]+$/.test(val) ) ) throw `Namespace will accept only 'lowercase' letter and 'Underscore' char`;
				this.setDataValue( 'namespace' , val.substring(0,45).toLowerCase() );
			},
			get(){
				return this.getDataValue('namespace').toLowerCase();
			}
		},
		password: {
			allowNull: false,
			type: DataTypes.STRING(255),
			set(val){
				let hash = bcrypt.hashSync( val , bcrypt.genSaltSync(14) );
				this.setDataValue('password',hash);
			},
		},
		enabled: {
			allowNull: false,
			type: DataTypes.BOOLEAN,
			defaulValue: true,
		}
	}, {
		classMethods: {
			associate: function(models) {
				// Has
				company.hasMany(models.user, {foreignKey: 'company_id'});
				company.hasMany(models.session_history, {foreignKey: 'company_id'});
				company.hasMany(models.handset, {foreignKey: 'company_id'});
			}
		}
	});
	return company;
};
