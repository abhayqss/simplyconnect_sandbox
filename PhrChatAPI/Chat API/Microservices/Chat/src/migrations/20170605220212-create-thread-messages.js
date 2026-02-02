'use strict';
module.exports = {
	up: function(queryInterface, Sequelize) {
		return queryInterface.createTable('thread_messages', {
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.BIGINT.UNSIGNED
			},
			sender: {
				type: Sequelize.BIGINT.UNSIGNED,
				allowNull: false,
				onUpdate: "CASCADE",
				onDelete: "CASCADE",
				references: {
					model: 'users',
					key: 'id'
				}
			},
			receiver: {
				type: Sequelize.BIGINT.UNSIGNED,
				allowNull: false,
				onUpdate: "CASCADE",
				onDelete: "CASCADE",
				references: {
					model: 'threads',
					key: 'id'
				}
			},
			type: {
				allowNull: false,
				type: Sequelize.ENUM('text','image','audio','video','stream'),
				defaultValue: 'text',
			},
			text: {
				allowNull: false,
				type: Sequelize.STRING(255)
			},
			createdAt: {
				allowNull: false,
				type: Sequelize.DATE,
				defaultValue: Sequelize.literal('now()'),
			},
			notifiedAt: {
				allowNull: true,
				type: Sequelize.DATE,
			},
			deliveredAt: {
				allowNull: true,
				type: Sequelize.DATE,
			}
		},
		{
			engine:'InnoDB',
			charset: 'utf8'
		});
	},
	down: function(queryInterface, Sequelize) {
		return queryInterface.dropTable('thread_messages');
	}
};
