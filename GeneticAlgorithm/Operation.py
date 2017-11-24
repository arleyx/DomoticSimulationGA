#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random
from Individual import Individual

class Operation():

	def __init__(self, connections):
		self.connections = connections

	def operate(self, parent_1, parent_2):
		childrens = self.crossover(parent_1, parent_2)
		return self.mutation(childrens[0], childrens[1])
		# return (self.crossover(parent_1, parent_2) if random.random() > 0.4 else self.mutation(parent_1, parent_2))

	def mutation(self, parent_1, parent_2):
		childrens = []

		change = random.randint(0, len(parent_1.genes[0]) - 1)
		for i in range(10):
			if parent_1.genes[1][change] > 0:
				change = random.randint(0, len(parent_1.genes[0]) - 1)
			else:
				connection = self.connections[parent_1.genes[0][change]]
				parent_1.genes[0][change] = connection[random.randint(0, len(connection) - 1)]
				break

		change = random.randint(0, len(parent_2.genes[0]) - 1)
		for i in range(10):
			if parent_2.genes[1][change] > 0:
				change = random.randint(0, len(parent_2.genes[0]) - 1)
			else:
				connection = self.connections[parent_2.genes[0][change]]
				parent_2.genes[0][change] = connection[random.randint(0, len(connection) - 1)]
				break

		childrens.append(Individual(parent_1.genes, parent_1.costs))
		childrens.append(Individual(parent_2.genes, parent_2.costs))

		return childrens

	def crossover(self, parent_1, parent_2):
		childrens = []

		part = random.randint(1, len(parent_1.genes[0]) - 1)

		childrens.append(
			Individual([
				parent_1.genes[0][0:part] + parent_2.genes[0][part:len(parent_2.genes[0])],
				parent_1.genes[1]
			], parent_1.costs)
		)
		childrens.append(
			Individual([
				parent_2.genes[0][0:part] + parent_1.genes[0][part:len(parent_1.genes[0])],
				parent_2.genes[1]
			], parent_2.costs)
		)

		return childrens