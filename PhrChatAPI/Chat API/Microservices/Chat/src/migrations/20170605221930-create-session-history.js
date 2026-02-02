'use strict';
module.exports = {
	up: function(queryInterface, Sequelize) {
		return queryInterface.createTable('session_histories', {
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.BIGINT.UNSIGNED
			},
			ip: {
				type: Sequelize.STRING(45)
			},
			in : {
				allowNull: false,
				type: Sequelize.DATE,
				defaultValue: Sequelize.literal('now()')
			},
			out: {
				allowNull: true,
				type: Sequelize.DATE,
				defaultValue: null
			},
			user_id: {
				type: Sequelize.BIGINT.UNSIGNED,
				allowNull: false,
				onUpdate: "CASCADE",
				onDelete: "CASCADE",
				references: {
					model: "users",
					key: "id"
				}
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
			handset_id: {
				type: Sequelize.BIGINT.UNSIGNED,
				allowNull: false,
				onUpdate: "CASCADE",
				onDelete: "CASCADE",
				references: {
					model: "handsets",
					key: "id"
				}
			},
		},
		{
			engine:'InnoDB',
			charset: 'utf8'
		});
	},
	down: function(queryInterface, Sequelize) {
		return queryInterface.dropTable('session_histories');
	}
};
