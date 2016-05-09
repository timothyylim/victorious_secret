import xml.etree.ElementTree as ET
import copy
import numpy as np

#-----------NOTES----------------------------
# OVERVIEW: This module is designed to parse battlecode replay files
# into a data format that can easily be manipulated by python functions.
#
# API: Outside modules should only call 'parseReplayFile(filename)'
# as this method will return replay information. All other methods
# are helper functions called within the 'parseReplayFile' method
# and should not be called directly
#
# RETURNED OBJECTS:
# gameInformation -> A list of dictionaries. Each dictionary is a match
# unitInformation -> A nested list ultimately containing robotInfo
#     -Top Level = list of matches, each match is also a list
#     -Match Lists = each match is a list containing a list of rounds
#     -Round Lists = each round is a list of dictionaries of robotInfo
#     -Dictionaries of RobotInfo = each dictionary has 1 key 'robotID'
#       the attribute of the key is a dictionary containing a robot's info
#     -RobotInfo dictionary = contains keys such as 'health', 'type', etc.
# otherGameplayInformation -> A catch all for all other information that
#  might be of interest
#     -NOT YET IMPLIMENTED
#--------------------------------------------

#-----------MAIN FUNCTION--------------------

#Top level function. Returns gameInformation and unitInformation
def parseReplayFile(filename):
    
    #Declare all replay information variables to be returned
    gameInformation = []
    unitInformation = []
    otherGameplayInformation = []
    #Attempt to parse filename. Exit on exception
    try:
        tree = ET.parse(filename)
        root = tree.getroot()
    except:
        print('Error parsing file: ' + filename)
        return [gameInformation,unitInformation]
    #Ensure that the root being parsed is expected
    if root.tag != 'object-stream':
        print(filename + ' is not in the expected format')
        return [gameInformation,unitInformation]
        
    #Loop through each child node and parse accordingly
    children = root.getchildren()
    for child in children:
        childTag = child.tag
        if childTag == 'ser.ExtensibleMetadata':
            parseExtensibleMetadata(child,gameInformation)
            #Add an empty list to unitInformationPerRound indicating
            #that a new match has started
            unitInformation.append([])
            otherGameplayInformation.append([])
        elif childTag == 'ser.RoundDelta':
            parseRoundDelta(child,unitInformation, otherGameplayInformation)
        elif childTag == 'ser.GameStats':
            parseGameStats(child,gameInformation)
        elif childTag == 'ser.MatchFooter':
            parseMatchFooter(child,gameInformation)
    return [gameInformation, unitInformation]

#-----------LEVEL 2 TAG FUNCTIONS--------------

#Creates a new gameInformationDictionary corresponding to the current game
#Fills that dictionary with data found in ExtensibleMetadata tag
def parseExtensibleMetadata(root, gameInformation):
    #Ensure that the correct node is being parsed
    if(root.tag != 'ser.ExtensibleMetadata'):
        return
    #Declare round information variables to be added to gameInformation
    gameInformationDictionary = {}
    #Cycle through all relevent keys and add values to gameInfoDictionary
    keys = ['team-a','team-b']
    metaData = root.attrib
    for key in keys:
        gameInformationDictionary[key]=metaData[key]
    #Extract the current map being played
    maps = metaData['maps'].split(',')
    gameInformationDictionary['map'] = maps[len(gameInformation)]
    #Append the new information to gameInformation
    gameInformation.append(gameInformationDictionary)
            
#Fills unitInformation with list of individual unit info dictionaries
def parseRoundDelta(root, unitInformation, otherGameplayInformation):
    #Ensure that the correct node is being parsed
    if(root.tag != 'ser.RoundDelta'):
        return
    #Declare round information variables
    currentMatchIndex = len(unitInformation)-1
    lastRoundIndex = len(unitInformation[currentMatchIndex])-1
    currentRound = {}
    if lastRoundIndex != -1:
        currentRound = copy.deepcopy(unitInformation[currentMatchIndex][lastRoundIndex])
    #Loop through each child node and parse accordingly
    children = root.getchildren()
    for child in children:
        childTag = child.tag
        if childTag=='sig.SpawnSignal':
            parseSpawnSignal(child,currentRound)
        elif childTag=='sig.MovementSignal':
            parseMovementSignal(child,currentRound)
        elif childTag=='sig.DeathSignal':
            parseDeathSignal(child,currentRound)
        elif childTag=='sig.HealthChangeSignal':
            parseHealthChangeSignal(child,currentRound)
        elif childTag=='sig.AttackSignal':
            continue
    unitInformation[currentMatchIndex].append(currentRound)
            
#Adds information to gameInformation from gameStats tag
def parseGameStats(root, gameInformation):
    #Ensure that the correct node is being parsed
    if(root.tag != 'ser.GameStats'):
        return
    #Cycle through all relevent keys and add values to gameInfoDictionary
    gameStats = root.attrib
    key = 'dominationFactor'
    currentIndex = len(gameInformation)-1
    gameInformation[currentIndex][key] = gameStats[key]

#Adds information to gameInformation from matchFooter tag
def parseMatchFooter(root, gameInformation):
    #Ensure that the correct node is being parsed
    if(root.tag != 'ser.MatchFooter'):
        return
    #Cycle through all relevent keys and add values to gameInfoDictionary
    footer = root.attrib
    key = 'winner'
    currentIndex = len(gameInformation)-1
    gameInformation[currentIndex][key] = footer[key]


#-----------LEVEL 3 TAG FUNCTIONS--------------

#PARENT METHOD: parseRoundDelta
def parseSpawnSignal(root, currentRound):
    if root.tag != 'sig.SpawnSignal':
        return
    spawnSignal = root.attrib
    keys = ['loc','type','team']
    robotInfo = {}
    for key in keys:
        robotInfo[key]=spawnSignal[key]
    currentRound[spawnSignal['robotID']]=robotInfo

#PARENT METHOD: parseRoundDelta
def parseMovementSignal(root, currentRound):
    if root.tag != 'sig.MovementSignal':
        return
    movementSignal = root.attrib
    keys = ['robotID','newLoc','loc']
    currentRound[movementSignal[keys[0]]][keys[2]] = movementSignal[keys[1]]

#PARENT METHOD: parseRoundDelta
def parseDeathSignal(root, currentRound):
    if root.tag != 'sig.DeathSignal':
        return
    deathSignal = root.attrib
    key = deathSignal['objectID']
    del currentRound[key]

#PARENT METHOD: parseRoundDelta
def parseHealthChangeSignal(root, currentRound):
    if root.tag != 'sig.HealthChangeSignal':
        return
    healthChange = root.attrib
    robotIDs = healthChange['robotIDs'].split(',')
    newHealth = healthChange['health'].split(',')
    for i in range(0,len(robotIDs)):
        currentRound[robotIDs[i]]['health']=newHealth[i]

