class Individual:
	def __init__(self, genes, costs):
		self.genes = genes
		self.costs = costs
		self.fitness = self.fitness()

	def fitness(self):
		fitness = self.get_cost(self.genes[0][0])

		for i in range(1, len(self.genes[0]) - 1):
			fitness += self.get_cost(self.genes[0][i]) + (self.get_cost_change(self.genes[0][i - 1]) if self.genes[0][i] != self.genes[0][i - 1] else 0)

		return fitness
	
	def get_cost(self, state):
		return self.costs[state] / 60

	def get_cost_change(self, state):
		return (self.costs[state] * 2) / 60