#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys, random, json, operator, time, os, csv
from Reader import Reader
from Population import Population

def main():
	print("Initialite...")

	init_time = time.time()

	reader = Reader(sys.argv[1])
	data = json.loads(sys.argv[2])

	actuators = data['actuators']
	components = data['components']

	#

	pathComponents = sys.argv[1].replace('GA.csv', 'components/')
	if not os.path.exists(pathComponents):
		os.makedirs(pathComponents)

	# reader.print_file()

	number_individuals = 250
	number_generations = 100

	actuators_genes = []
	for actuator in actuators:
		actuators_genes.append({'name':actuator, 'genes':reader.get_genes_user(actuator)})

	components_name = []
	individuals = []
	for c in components:

		print('\n')
		print(c)

		genes = reader.get_genes_device(components[c]['place'], c, actuators)
		connections = components[c]['connections']
		costs = components[c]['costs']
		population = Population(number_individuals, number_generations, genes, connections, costs)

		individuals.append({'order':index_component(c, reader.content[0]), 'name': c, 'place': components[c]['place'], 'optimized':population.optimized, 'genes':population.best.genes[0], 'prev':population.individual.fitness, 'last':population.best.fitness})

		components_name.append(c)

		export_component(pathComponents, c, {
			'name': c,
			'place': components[c]['place'],
			'before': {
				'fitness': population.individual.fitness,
				'genes': genes[0]
			},
			'after': {
				'fitness': population.best.fitness,
				'genes': population.best.genes[0]
			},
			'users': actuators_genes
		})

		print('\nNAME:' + c + ', PLACE:' + components[c]['place'] + ', FITNESS:' + str(population.best.fitness) + '\n')
		print(population.best.genes)


	#######
	# Descomentar cuando se terminen de cargar las 30 veces de los resultados
	#
	export_component(pathComponents, 'index', { 'components': components_name })
	#
	# Y comentar
	export_all_data(individuals, actuators_genes, time.time() - init_time)
	#######

	individuals = sorted(individuals, key=operator.itemgetter('order'))

	reader.write(export_data(individuals, actuators_genes, time.time() - init_time))

def export_component(path, name, data):
	outfile = open(path + name + '.json', 'w')
	json.dump(data, outfile)

def export_all_data(individuals, actuators, time):
	data = []
	row = []

	sum = 0
	for individual in individuals:
		sum += individual['prev']
	row.append(sum)

	sum = 0
	for individual in individuals:
		sum += individual['last']
	row.append(sum)

	sum = 0
	for individual in individuals:
		if individual['optimized']:
			sum += 1

	row.append(len(individuals))
	row.append(sum)
	row.append(time)

	data.append(row)

	content = []
	file = open('/home/arley/results.csv')
	reader = csv.reader(file)
	for line in reader:
		content.append(line)

	writer = csv.writer(open('/home/arley/results.csv','w'))
	writer.writerows(content)
	writer.writerows(data)

def export_data(individuals, actuators, time):
	data = [[]]

	data.append(['Tiempo de ejecución (s)', time])

	data.append([])

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
