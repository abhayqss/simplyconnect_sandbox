'use strict';
module.exports = {
	up: function(queryInterface, Sequelize) {
		return queryInterface.createTable('handsets', {
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.BIGINT.UNSIGNED,
			},
			uuid: {
				allowNull: false,
				unique: true,
				type: Sequelize.STRING(38)
			},
			pn_token: {
				allowNull: true,
				type: Sequelize.STRING(200)
			},
			type: {
				allowNull: false,
				type: Sequelize.ENUM('android','ios'),
			},
			company_id: {
				type: Sequelize.BIGINT.UNSIGNED,
				onUpdate: "CASCADE",
				onDelete: "CASCADE",
				allowNull: false,
				references: {
					model: "companies",
					key: "id"
				}
			},
			createdAt: {
				allowNull: false,
				type: Sequelize.DATE,
				defaultValue: Sequelize.literal('now()'),
			},
			updatedAt: {
				allowNull: true,
				type: Sequelize.DATE
			}
		},
		{
			engine:'InnoDB',
			charset: 'utf8'
		});
	},
	down: function(queryInterface, Sequelize) {
		return queryInterface.dropTable('handsets');
	}
};
