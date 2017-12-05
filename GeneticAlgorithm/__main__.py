#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys, random, json, operator
from Reader import Reader
from Population import Population

def main():
	print("Initialite...")

	reader = Reader(sys.argv[1])
	data = json.loads(sys.argv[2])

	actuators = data['actuators']
	components = data['components']

	# reader.print_file()

	number_individuals = 250
	number_generations = 100

	individuals = []
	for c in components:

		print('\n')
		print(c)

		genes = reader.get_genes_device(components[c]['place'], c, actuators)
		connections = components[c]['connections']
		costs = components[c]['costs']
		population = Population(number_individuals, number_generations, genes, connections, costs)

		individuals.append({'order':index_component(c, reader.content[0]), 'name': c, 'place': components[c]['place'], 'optimized':population.optimized, 'genes':population.best.genes[0], 'prev':population.individual.fitness, 'last':population.best.fitness})

		print('\nNAME:' + c + ', PLACE:' + components[c]['place'] + ', FITNESS:' + str(population.best.fitness) + '\n')
		print(population.best.genes)

	individuals = sorted(individuals, key=operator.itemgetter('order'))

	actuators_genes = []
	for actuator in actuators:
		actuators_genes.append({'name':actuator, 'genes':reader.get_genes_user(actuator)})

	reader.write(export_data(individuals, actuators_genes))

def export_data(individuals, actuators):
	data = [[]]

	# RESULTADOS FITNESS

	row = ['']
	for individual in individuals:
		row.append(individual['name'])
	row.append('TOTAL')
	data.append(row)

	sum = 0
	row = ['Fitness anterior']
	for individual in individuals:
		row.append(individual['prev'])
		sum += individual['prev']
	row.append(sum)
	data.append(row)

	sum = 0
	row = ['Fitness despues']
	for individual in individuals:
		row.append(individual['last'])
		sum += individual['last']
	row.append(sum)
	data.append(row)

	row = ['Recomendaciones']
	for individual in individuals:
		row.append('Si' if individual['optimized'] else 'No')
	data.append(row)

	data.append([])

	# RECOMENDACIONES

	rowPlace = ['']
	row = ['']
	for individual in individuals:
		if individual['optimized']:
			rowPlace.append(individual['place'])
			row.append(individual['name'])
			for actuator in actuators:
				rowPlace.append('')
				row.append(actuator['name'])
				rowPlace.append('')
				row.append('')
	data.append(rowPlace)
	data.append(row)

	for i in range(0, len(individuals[0]['genes'])):
		row = ['']
		for individual in individuals:
			if individual['optimized']:
				row.append(individual['genes'][i])
				for actuator in actuators:
					row.append(actuator['genes'][i])
					row.append('')
		data.append(row)

	return data

def index_component(component, components):
	i = 0
	for _component in components:
		if component == _component:
			return i
		i += 1

if __name__ == "__main__":
	main()
