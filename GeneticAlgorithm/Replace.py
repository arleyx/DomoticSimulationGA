#!/usr/bin/env python
# -*- coding: utf-8 -*-

import random

class Replace:

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

    def compete(self, individual_1, individual_2):
        fitness_total = individual_1.fitness + individual_2.fitness

        percentage_1 = (individual_1.fitness / fitness_total)

        return (individual_2 if random.random() > percentage_1 else individual_1)