'use strict';
module.exports = {
	up: function(queryInterface, Sequelize) {
		return queryInterface.createTable('companies', {
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.BIGINT.UNSIGNED
			},
			notifyCompanyId: {
				allowNull: false,
				type: Sequelize.INTEGER.UNSIGNED
			},
			name: {
				type: Sequelize.STRING
			},
			namespace: {
				allowNull: false,
				unique: true,
				type: Sequelize.STRING(45)
			},
			password: {
				allowNull: false,
				type: Sequelize.STRING(255)
			},
			enabled: {
				allowNull: false,
				type: Sequelize.BOOLEAN,
				defaultValue: true,
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
		return queryInterface.dropTable('companies');
	}
};
