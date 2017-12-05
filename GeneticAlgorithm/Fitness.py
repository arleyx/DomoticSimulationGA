#!/usr/bin/env python
# -*- coding: utf-8 -*-

class Fitness:
    def __init__(self, genes_user, costs):
        self.costs = costs
        self.genes_user = genes_user
        self.info = {}

    def get_fitness(self, genes):
        change = 0
        penalization = 0
        fitness = self.get_cost(genes[0][0])

        for i in range(1, len(genes[0]) - 1):
            # SI CAMBIA DE ESTADO, PENALIZE CON EL COSTO DE CAMBIO
            if genes[0][i] != genes[0][i - 1]:
                change += self.get_cost_change(genes[0][i - 1])

            # SI APAGA CUANDO EL USUARIO ESTA, PENALIZE
            if genes[1][i] == 1 and genes[0][i] != self.genes_user[0][i]:
                penalization += self.get_cost_penalization()

            fitness += self.get_cost(genes[0][i])

        self.info = {'change':change, 'penalization':penalization, 'fitness':fitness}

        return change + penalization + fitness

    def get_cost(self, state):
        return self.costs[state] / 60

    def get_cost_change(self, state):
        return (self.costs[state] / 60) * 2

    def get_cost_penalization(self):
        return (self.get_max_cost() / 60) * 5

    def get_max_cost(self):
        max = 0
        for i in self.costs:
            if (self.costs[i] > max):
                max = self.costs[i]

        return max
