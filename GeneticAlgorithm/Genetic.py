#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random
from Individual import Individual

class Genetic:

	def __init__(self, n, individual, connections):
		self.connections = connections

		# Primera generación
		last_generation = self.generate(100, individual)

		if n > 1:
			for i in range(2, n + 1):
				last_generation = self.new_generation(last_generation)

		self.best = self.the_best(last_generation)
		self.optimized = True

		print('')
		print('')
		print('')
		print(self.best.fitness)
		print('')
		print(self.best.genes)

		

		# SI NO ES MEJOR, DEJA EL INICIAL
		if self.best.fitness >= individual.fitness:
			self.best = individual
			self.optimized = False

		''' print("\n========================================================")
		print("Individuo inicial:\033[0m")
		print(individual.genes[0])
		print(individual.genes[1])
		print("Aptitud: \033[7m\033[91m" + str(individual.fitness) + "\033[0m")
		print("\nIndividuo final:\033[0m")
		print(best.genes[0])
		print(best.genes[1])
		print("Aptitud: \033[7m\033[92m" + str(best.fitness) + "\033[0m")
		print("========================================================\n") '''

	def generate(self, n, individual):
		individuals = []
		for i in range(n):

			print('')
			print('')
			print('Individuo #' + str(i + 1))

			genes = [[],individual.genes[1]]
			for j in range(0, len(individual.genes[0])):
				if individual.genes[1][j] == 1:
					genes[0].append(individual.genes[0][j])
				else:
					connection = self.connections[individual.genes[0][j]]
					genes[0].append(connection[random.randint(0, len(connection) - 1)])

			_individual = Individual(genes, individual.costs)

			print('Costo:' + str(_individual.fitness))
			print('')
			print(genes)

			individuals.append(_individual)
		return individuals

	def new_generation(self, last_generation):
		new_generation = []
		for i in range(int(len(last_generation) / 2)):
			parents = self.selection(last_generation)
			childrens = self.operate(parents[0], parents[1])
			new_individuals = self.replace(parents, childrens)

			new_generation.append(new_individuals[0])
			new_generation.append(new_individuals[1])

		return new_generation

	def selection(self, individuals):
		if len(individuals) > 4:
			selection = []
			parents = []

			for i in range(4):
				selector = random.randint(0, len(individuals) - 1)
				selection.append(individuals[selector])

			for i in range(2):
				parents.append(self.compete(selection[0], selection[1]))

				selection.pop(1)
				selection.pop(0)

			return parents

	def compete(self, individual_1, individual_2):
		winner = []

		fitness_total = individual_1.fitness + individual_2.fitness

		percentage_1 = (individual_1.fitness / fitness_total)

		return (individual_2 if random.random() > percentage_1 else individual_1)

	def operate(self, parent_1, parent_2):
		# 60% cruce, 40% mutación
		return (self.crossover(parent_1, parent_2) if random.random() > 0.5 else self.mutation(parent_1, parent_2))

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

	def replace(self, parents, childrens):
		#return childrens
		return self.steady_state(parents, childrens)

	def steady_state(self, parents, childrens):
		new_parents = []

		best_parent = []
		worse_parent = []
		best_children = []
		worse_children = []

		if parents[0].fitness > parents[1].fitness:
			best_parent = parents[0]
			worse_parent = parents[1]
		else:
			best_parent = parents[1]
			worse_parent = parents[0]

		if childrens[0].fitness > childrens[1].fitness:
			best_children = childrens[0]
			worse_children = childrens[1]
		else:
			best_children = childrens[1]
			worse_children = childrens[0]

		new_parents.append(self.compete(best_parent, best_children))
		new_parents.append(self.compete(worse_parent, worse_children))

		return new_parents

	def the_best(self, individuals):
		best_individual = individuals[0]

		for i in range(1, len(individuals)):
			temp_fitness = individuals[i].fitness
			if (individuals[i].fitness < best_individual.fitness):
				best_individual = individuals[i]

		return best_individual

	def print_poblation(self, individuals, description):
		print("\n\033[101mPoblación: " + str(len(individuals)) + " individuos, " + description + "\033[0m\n")
		for individual in individuals:
			print(individual.genes[0])
			print(individual.genes[1])
			print("Aptitud: " + str(individual.fitness) + "\n")
