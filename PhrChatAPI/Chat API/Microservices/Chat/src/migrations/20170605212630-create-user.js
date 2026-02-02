'use strict';
module.exports = {
	up: function(queryInterface, Sequelize) {
		return queryInterface.createTable('users', {
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.BIGINT.UNSIGNED
			},
			notifyUserId: {
				allowNull: false,
				type: Sequelize.INTEGER.UNSIGNED
			},
			name: {
				allowNull: false,
				type: Sequelize.STRING
			},
			logged: {
				allowNull: false,
				type: Sequelize.BOOLEAN,
				defaultValue: false,
			},
			role: {
				allowNull: false,
				type: Sequelize.ENUM('user','admin'),
				defaultValue: 'user',
			},
			current_handset: {
				allowNull: true,
				unique: true,
				type: Sequelize.STRING(38)
			},
			company_id: {
				type: Sequelize.BIGINT.UNSIGNED,
				allowNull: false,
				onUpdate: "CASCADE",
				onDelete: "CASCADE",
				references: {
					model: "companies",
					key: "id"
				}
			},
			timezone_id: {
				type: Sequelize.INTEGER.UNSIGNED,
				allowNull: false,
				references: {
					model: "timezones",
					key: "id"
				}
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
		return queryInterface.dropTable('users');
	}
};
