'use strict';
module.exports = function(sequelize, DataTypes) {
	var handset = sequelize.define('handset', {
		id: {
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.INTEGER
		},
		uuid: {
			allowNull: false,
			unique: true,
			type: DataTypes.STRING(38),
			set(val){
				this.setDataValue( 'uuid' , val.toUpperCase() );
			},
		},
		pn_token: {
			allowNull: true,
			type: DataTypes.STRING(200)
		},
		type: {
			allowNull: false,
			type: DataTypes.ENUM('android','ios'),
		},
		company_id: {
			type: DataTypes.BIGINT.UNSIGNED,
			allowNull: false,
		},
		device_name: {
			type: DataTypes.STRING(100),
			allowNull: false
		}
	}, {
		classMethods: {
			associate: function(models) {
				// Belongs
				handset.belongsTo(models.company, {foreignKey: 'company_id'});

				// Has
				handset.hasMany(models.session_history, {foreignKey: 'handset_id'});
			}
		}
	});
	return handset;
};
