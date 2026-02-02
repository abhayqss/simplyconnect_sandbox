'use strict';

module.exports = {
	up: function (queryInterface, Sequelize) {
		return queryInterface.createTable('thread_participants',{
			id: {
				allowNull: false,
				autoIncrement: true,
				primaryKey: true,
				type: Sequelize.BIGINT.UNSIGNED
			},
			thread_id: {
				type: Sequelize.BIGINT.UNSIGNED,
				allowNull: false,
				onUpdate: "CASCADE",
				onDelete: "CASCADE",
				references: {
					model: "threads",
					key: "id"
				}
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
		},
		{
			engine:'InnoDB',
			charset: 'utf8'
		})
		.then(function(){
			return queryInterface.addIndex(
				'thread_participants',
				['thread_id','user_id'],
				{
					indexName: 'thread_participant_unique_tuple',
					indicesType: 'UNIQUE',
				}
			);
		});
	},
	down: function(queryInterface, Sequelize) {
		return queryInterface.dropTable('thread_participants');
	}
};
