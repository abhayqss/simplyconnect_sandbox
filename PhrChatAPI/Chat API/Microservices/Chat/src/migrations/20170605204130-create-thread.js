'use strict';
module.exports = {
	up: function(queryInterface, Sequelize) {
		return queryInterface.createTable('threads', {
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.BIGINT.UNSIGNED
			},
			name: {
				type: Sequelize.STRING(45)
			},
			description: {
				type: Sequelize.STRING
			},
			quantity: {
				allowNull: false,
				type: Sequelize.INTEGER.UNSIGNED
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
		return queryInterface.dropTable('threads');
	}
};
