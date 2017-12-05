#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random
from Individual import Individual

class Operation:

	def __init__(self, fitness, connections):
		self.fitness = fitness
		self.connections = connections

	def operate(self, parent_1, parent_2):
		childrens = self.crossover(parent_1, parent_2)
		return self.mutation(childrens[0], childrens[1])

		# childrens = self.mutation(parent_1, parent_2)
		# return self.crossover(childrens[0], childrens[1])

		# return (self.crossover(parent_1, parent_2) if random.random() > 0.4 else self.mutation(parent_1, parent_2))

	def mutation(self, parent_1, parent_2):
		childrens = []

		states = list(self.connections.keys())

		change = random.randint(0, len(parent_1.genes[0]) - 1)
		for i in range(10):
			if parent_1.genes[1][change] > 0:
				change = random.randint(0, len(parent_1.genes[0]) - 1)
			else:

				parent_1.genes[0][change] = states[random.randint(0, len(states) - 1)]
				parent_1 = self.rebuild(parent_1, change)

				break

		change = random.randint(0, len(parent_2.genes[0]) - 1)
		for i in range(10):
			if parent_2.genes[1][change] > 0:
				change = random.randint(0, len(parent_2.genes[0]) - 1)
			else:

				parent_2.genes[0][change] = states[random.randint(0, len(states) - 1)]
				parent_2 = self.rebuild(parent_2, change)

				# connection = self.connections[parent_2.genes[0][change]]
				# parent_2.genes[0][change] = connection[random.randint(0, len(connection) - 1)]
				break

		childrens.append(Individual(parent_1.genes, self.fitness.get_fitness(parent_1.genes)))
		childrens.append(Individual(parent_2.genes, self.fitness.get_fitness(parent_2.genes)))

		return childrens

	def crossover(self, parent_1, parent_2):
		childrens = []

		part = random.randint(1, len(parent_1.genes[0]) - 1)

		genes1 = [
			parent_1.genes[0][0:part] + parent_2.genes[0][part:len(parent_2.genes[0])],
			parent_1.genes[1]
		]
		children1 =  Individual(genes1, self.fitness.get_fitness(genes1))
		children1 = self.rebuild(children1, part)

		genes2 = [
			parent_2.genes[0][0:part] + parent_1.genes[0][part:len(parent_1.genes[0])],
			parent_2.genes[1]
		]
		children2 = Individual(genes2, self.fitness.get_fitness(genes2))
		children2 = self.rebuild(children2, part)

		childrens.append(children1)
		childrens.append(children2)

		return childrens

	def rebuild(self, parent, change):
		rebuild_parent = self.prev_rebuild(parent, change - 1)
		return self.next_rebuild(rebuild_parent, change + 1)

	def prev_rebuild(self, parent, change):
		if change < 0:
			return parent

		connection = self.get_prev_connection(parent.genes[0][change + 1])
		if parent.genes[0][change] in connection:
			return parent

		if change > 0 and parent.genes[0][change] == parent.genes[0][change - 1]:
			return parent

		connection.append(parent.genes[0][change + 1])

		parent.genes[0][change] = connection[random.randint(0, len(connection) - 1)]

		return self.prev_rebuild(parent, change - 1)

	def next_rebuild(self, parent, change):
		if change > len(parent.genes[0]) - 1:
			return parent

		connection = self.connections[parent.genes[0][change - 1]]
		if parent.genes[0][change] in connection:
			return parent

		if change < len(parent.genes[0]) - 1 and parent.genes[0][change] == parent.genes[0][change + 1]:
			return parent

		connection.append(parent.genes[0][change - 1])

		parent.genes[0][change] = connection[random.randint(0, len(connection) - 1)]

		return self.next_rebuild(parent, change + 1)

	def get_prev_connection(self, state):
		connections = []

		for key in self.connections:
			if state in self.connections[key]:
				connections.append(key)

		return connections
