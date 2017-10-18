class Individual:
	def __init__(self, genes):
		self.genes = genes
		self.fitness = self.fitness()

	def fitness(self):
		cost = 60
		cost_change = 20
	
		fitness = self.genes[0][0] * cost

		for i in range(1, len(self.genes[0])):
			fitness += (self.genes[0][i] * cost) + (cost_change if self.genes[0][i] > self.genes[0][i - 1] else 0)
		
		return fitness
