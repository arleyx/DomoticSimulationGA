#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random
from Individual import Individual
from Operation import Operation
from Replace import Replace

class Population:
	
	def __init__(self, n, g, individual, connections):
		self.n = n
		self.g = g
		self.individual = individual
		self.connections = connections

		self.population = self.initial_population()

		self.operation = Operation(connections)
		self.replace = Replace()

		self.generations()
		
	def initial_population(self):
		individuals = []
		for i in range(self.n):
			genes = [[], self.individual.genes[1]]

			penalization = 0

			for j in range(0, len(self.individual.genes[0])):
				#if self.individual.genes[1][j] == 1:
					#genes[0].append(self.individual.genes[0][j])
				#else:
				connection = self.connections[self.individual.genes[0][j]] + [self.individual.genes[0][j]]
				_random = random.randint(0, len(connection) - 1)
				genes[0].append(connection[_random])

				if self.individual.genes[1][j] == 1:
					penalization += self.individual.get_cost(connection[_random]) * 5

			_individual = Individual(genes, self.individual.costs)

			# print('Antes: ' + str(_individual.fitness))

			_individual.fitness += penalization

			# print('Despues: ' + str(_individual.fitness))

			individuals.append(_individual)
		return individuals

	def generations(self):
		if self.g > 1:
			for i in range(2, self.g + 1):
				self.population = self.new_generation(self.population)

		self.best = self.the_best()
		self.optimized = True

		
		print('BEST')
		print('Fitness: ' + str(self.best.fitness))
		print('')
		print(self.best.genes)
		

		# SI NO ES MEJOR, DEJA EL INICIAL
		if self.best.fitness >= self.individual.fitness:
			self.best = self.individual
			self.optimized = False

	def new_generation(self, last_generation):
		new_generation = []
		for i in range(int(len(last_generation) / 2)):
			parents = self.selection(last_generation)

			# = self.operate(parents[0], parents[1])
			childrens = self.operation.operate(parents[0], parents[1])
			new_individuals = self.replace.replace(parents, childrens)

			new_generation.append(new_individuals[0])
			new_generation.append(new_individuals[1])

		return new_generation

	def selection(self, population):
		if len(population) > 4:
			selection = []
			parents = []

			for i in range(4):
				selector = random.randint(0, len(population) - 1)
				selection.append(population[selector])

			for i in range(2):
				parents.append(self.compete(selection[0], selection[1]))

				selection.pop(1)
				selection.pop(0)

			return parents

	def compete(self, individual_1, individual_2):
		fitness_total = individual_1.fitness + individual_2.fitness

		percentage_1 = (individual_1.fitness / fitness_total)

		return (individual_2 if random.random() > percentage_1 else individual_1)

	def the_best(self):
		best = self.population[0]

		for i in range(1, len(self.population)):
			temp_fitness = self.population[i].fitness
			if (self.population[i].fitness < best.fitness):
				best = self.population[i]

		return best