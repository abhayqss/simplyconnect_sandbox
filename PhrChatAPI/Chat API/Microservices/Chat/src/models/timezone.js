'use strict';
module.exports = function(sequelize, DataTypes) {
	var timezone = sequelize.define('timezone', {
		id: {
			allowNull: false,
			autoIncrement: true,
			primaryKey: true,
			type: DataTypes.INTEGER.UNSIGNED
		},
		utc_offset: {
			allowNull: false,
			type: DataTypes.STRING(9),
			get(){
				return `UTC${this.getDataValue('utc_offset')}`;
			},
			set(value){
				if( value ) this.setDataValue( 'utc_offset' , value.replace(/UTC|utc/,'') );
			}
		},
		name: {
			allowNull: false,
			type: DataTypes.STRING,
		},
		abbreviation: {
			allowNull: false,
			type: DataTypes.STRING(4),
			set(value){
				if( value ) this.setDataValue( 'abbreviation' , value.toUpperCase() );
			},
			get(){
				return this.getDataValue('abbreviation').toUpperCase();
			}
		}
	}, {
		classMethods: {
			associate: function(models) {
				// Has
				timezone.hasMany(models.user, {foreignKey: 'timezone_id'});
			}
		}
	});
	return timezone;
};
