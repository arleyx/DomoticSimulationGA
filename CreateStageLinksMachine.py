#!/usr/bin/env python
# -*- coding: utf-8 -*-

from os import listdir, symlink, remove, getcwd
from os.path import isdir, isfile, join
from shutil import rmtree

def main():
    path_machines = getcwd() + '/Machines/'
    path_stages = getcwd() + '/Stages/'

    entry_recursive(0, path_stages, path_machines)

    # for dir in listdir(path_stages):
    #     new_path = path_stages + dir
    #     if (isdir(new_path)):
    #         print (dir)
    #         for subdir in listdir(new_path):
    #             new_sub_path = path_stages + dir + '/' + subdir
    #             if (isdir(new_sub_path)):
    #                 print ('\t' + subdir)

def entry_recursive(level, path, path_machines):
    for dir in listdir(path):
        new_path = join(path, dir)
        if (isdir(new_path)):
            # print (('\t' * level) + dir)
            if (level < 2):
                entry_recursive(level + 1, new_path, path_machines)
            else:
                if (dir == 'machines'):
                    create_symbolic(path_machines, new_path)
                    # print (('\t' * level) + '->' + dir)

def create_symbolic(path_machines, url):
    machines = [
        'Blender',
        'Charger',
        'Iron',
        'Lamp',
        'Microwave',
        'Pc',
        'Refrigerator',
        'Stereo',
        'Tv',
        'Washing'
    ]

    for machine in machines:
        source = join(path_machines, machine)
        link = join(url, machine)

        if isdir(link): rmtree(link)
        if isfile(link): remove(link)

        symlink(source, link)
        print ('Created link to: ' + link + '\nFrom: ' + source + '\n')

main()
