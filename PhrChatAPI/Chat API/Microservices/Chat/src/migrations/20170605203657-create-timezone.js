'use strict';
module.exports = {
	up: function(queryInterface, Sequelize) {
		return queryInterface.createTable('timezones', {
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.INTEGER.UNSIGNED
			},
			utc_offset: {
				allowNull: false,
				type: Sequelize.STRING(9)
			},
			name: {
				allowNull: false,
				type: Sequelize.STRING
			},
			abbreviation: {
				allowNull: false,
				type: Sequelize.STRING(5)
			},
			createdAt: {
				allowNull: false,
				type: Sequelize.DATE,
				defaultValue: Sequelize.literal('now()'),
			},
			updatedAt: {
				type: Sequelize.DATE,
				defaultValue: null,
			}
		},
		{
			engine:'InnoDB',
			charset: 'utf8'
		});
	},
	down: function(queryInterface, Sequelize) {
		return queryInterface.dropTable('timezones');
	}
};
