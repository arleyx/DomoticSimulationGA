import sys
import random
from Individual import Individual

def main():
	
	n = int(sys.argv[1]) if len(sys.argv) > 1 else 5;

	individual = Individual([
		[1 for i in range(500)],
		[random.randint(0,1) for i in range(500)]
	])
	
	# Primera generación
	last_generation = generate(100, individual)

	if n > 1:
		for i in range(2, n + 1):
			last_generation = new_generation(last_generation)

	best = the_best(last_generation)
	
	print "\n========================================================"
	print "Individuo inicial:\033[0m"
	print individual.genes[0]
	print individual.genes[1]
	print "Aptitud: \033[7m\033[91m" + str(individual.fitness) + "\033[0m"
	print "\nIndividuo final:\033[0m"
	print best.genes[0]
	print best.genes[1]
	print "Aptitud: \033[7m\033[92m" + str(best.fitness) + "\033[0m"
	print "========================================================\n"

def new_generation(last_generation):
	new_generation = []

	for i in range(len(last_generation) / 2):
		parents = selection(last_generation)
		childrens = operate(parents[0], parents[1])
		new_individuals = replace(parents, childrens)

		new_generation.append(new_individuals[0])
		new_generation.append(new_individuals[1])

	return new_generation

def generate(n, individual):
	individuals = []
	for i in range(n):
		individuals.append(
			Individual([
				[(1 if j == 1 else random.randint(0, 1)) for j in individual.genes[1]],
				individual.genes[1]
			])
		)
	return individuals

"""
def fitness(individual):
	cost = 60
	cost_change = 20

	fitness = individual.genes[0][0] * cost

	for i in range(1, len(individual.genes[0])):
		fitness += (individual.genes[0][i] * cost) + (cost_change if individual.genes[0][i] > individual.genes[0][i - 1] else 0)

	return fitness
"""

def selection(individuals):
	if len(individuals) > 4:
		selection = []
		parents = []

		for i in range(4):
			selector = random.randint(0, len(individuals) - 1)
			selection.append(individuals[selector])
		
		for i in range(2):
			parents.append(compete(selection[0], selection[1]))

			selection.pop(1)
			selection.pop(0)

		return parents

def compete(individual_1, individual_2):
	winner = []

	fitness_total = individual_1.fitness + individual_2.fitness

	percentage_1 = (individual_1.fitness / fitness_total)

	return (individual_2 if random.random() > percentage_1 else individual_1)

def operate(parent_1, parent_2):
	# 60% cruce, 40% mutación
	return (crossover(parent_1, parent_2) if random.random() > 0.4 else mutation(parent_1, parent_2))

def mutation(parent_1, parent_2):
	childrens = []

	change = random.randint(0, len(parent_1.genes[0]) - 1)
	for i in range(10):
		if parent_1.genes[1][change] > 0:
			change = random.randint(0, len(parent_1.genes[0]) - 1)
		else:
			parent_1.genes[0][change] = 0 if parent_1.genes[0][change] == 1 else 1
			break

	change = random.randint(0, len(parent_2.genes[0]) - 1)
	for i in range(10):
		if parent_2.genes[1][change] > 0:
			change = random.randint(0, len(parent_2.genes[0]) - 1)
		else:
			parent_2.genes[0][change] = 0 if parent_2.genes[0][change] == 1 else 1
			break

	childrens.append(parent_1)
	childrens.append(parent_2)

	return childrens

def crossover(parent_1, parent_2):
	childrens = []

	part = random.randint(1, len(parent_1.genes[0]) - 1)

	childrens.append(
		Individual([
			parent_1.genes[0][0:part] + parent_2.genes[0][part:len(parent_2.genes[0])],
			parent_1.genes[1]
		])
	)
	childrens.append(
		Individual([
			parent_2.genes[0][0:part] + parent_1.genes[0][part:len(parent_1.genes[0])],
			parent_2.genes[1]
		])
	)

	return childrens

def replace(parents, childrens):
	#return childrens
	return steady_state(parents, childrens)

def steady_state(parents, childrens):
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
	
	new_parents.append(compete(best_parent, best_children))
	new_parents.append(compete(worse_parent, worse_children))
	
	return new_parents

def the_best(individuals):
	best_individual = individuals[0]

	for i in range(1, len(individuals)):
		temp_fitness = individuals[i].fitness
		if (individuals[i].fitness < best_individual.fitness):
			best_individual = individuals[i]

	return best_individual

def print_poblation(individuals, description):
	print "\n\033[101mPoblación: " + str(len(individuals)) + " individuos, " + description + "\033[0m\n"
	for individual in individuals:
		print individual.genes[0]
		print individual.genes[1]
		print "Aptitud: " + str(individual.fitness) + "\n"

if __name__ == "__main__":
	main()
