#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random
from Individual import Individual
from Fitness import Fitness
from Operation import Operation
from Replace import Replace

class Population:

	def __init__(self, n, g, genes, connections, costs):
		self.n = n
		self.g = g
		self.connections = connections

		self.fitness = Fitness(genes, costs)
		self.operation = Operation(self.fitness, connections)
		self.replace = Replace()

		self.individual = Individual(genes, self.fitness.get_fitness(genes))
		self.population = self.initial_population()
		self.generations()

	def initial_population(self):
		individuals = []
		for i in range(self.n):
			genes = [[], self.individual.genes[1]]

			for j in range(0, len(self.individual.genes[0])):
				#if self.individual.genes[1][j] == 1:
					#genes[0].append(self.individual.genes[0][j])
				#else:
				connection = self.connections[self.individual.genes[0][j]] + [self.individual.genes[0][j]]
				genes[0].append(connection[random.randint(0, len(connection) - 1)])

			individuals.append(Individual(genes, self.fitness.get_fitness(genes)))
		return individuals

	def generations(self):
		number_generations = []

		if self.g > 1:
			for i in range(2, self.g + 1):
				self.population = self.new_generation(self.population)
				number_generations.append(self.the_best().fitness)

		text_generations = ''
		for i in number_generations:
			text_generations += str(i) + ','

		print('\nGeneraciones')
		print(text_generations)

		self.best = self.the_best()
		self.optimized = True

		self.fitness.get_fitness(self.best.genes)
		print('\n')
		print(self.fitness.info)

		# print('BEST')
		# print('Fitness: ' + str(self.best.fitness))
		# print('')
		# print(self.best.genes)


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

			for i in range(3): ### CAMBIE SOLO POR PROBAR PASANDO EL MEJOR Y OTROS 3, ESTABA EN 4.
				selector = random.randint(0, len(population) - 1)
				selection.append(population[selector])

			selection.append(self.the_best())

			for i in range(2):
				parents.append(self.compete(selection[0], selection[1]))

				selection.pop(1)
				selection.pop(0)

			return parents

	def compete(self, individual_1, individual_2):
		fitness_total = individual_1.fitness + individual_2.fitness

		if (fitness_total == 0): return individual_1

		percentage_1 = (individual_1.fitness / fitness_total)

		return (individual_2 if random.random() > percentage_1 else individual_1)

	def the_best(self):
		best = self.population[0]

		for i in range(1, len(self.population)):
			temp_fitness = self.population[i].fitness
			if (self.population[i].fitness < best.fitness):
				best = self.population[i]

		return best
