#!/usr/bin/env python
# -*- coding: utf-8 -*-

import csv, operator

class Reader:
    def __init__(self, pathfile):
        self.pathfile = pathfile
        self.file = open(self.pathfile)
        self.reader = csv.reader(self.file)
        self.content = []
        for line in self.reader:
            self.content.append(line)

    def write(self, data):
        writer = csv.writer(open(self.pathfile.replace('.csv','Processed.csv'),'w'))
        writer.writerows(self.content)
        writer.writerows(data)

    def get_genes_device(self, place, device, actuators):
        genes = [[],[]] 
        i = 0
        index_device = 0
        index_actuators = []
        for row in self.content:
            if i == 0:
                index_device = self.index_device(row, device)
                for actuator in actuators:
                    index_actuators.append(self.index_device(row, actuator))
            else:
                genes[0].append(row[index_device])
                genes[1].append(self.actuators_in_place(row, place, index_actuators))
            i += 1

        return genes

    def get_genes_user(self, actuator):
        genes = []
        i = 0
        index_actuator = -1
        for row in self.content:
            if i == 0:
                index_actuator = self.index_device(row, actuator)
            else:
                genes.append(row[index_actuator])
            i += 1

        return genes

    def index_device(self, header, device):
        i = 0
        for _device in header:
            if device == _device:
                return i
            i += 1

    def actuators_in_place(self, row, place, index_actuators):
        for i in index_actuators:
            if row[i] == place:
                return 1
        return 0

    def print_file(self):
        for line in self.content:
            print(line)
