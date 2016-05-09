import sys
import parseReplayFile as prp
import matplotlib.pyplot as plt

#Check that a filename was passed as a commandline argument
if len(sys.argv) !=2:
    print('Unexpected number of arguments')
    exit

#Parse replay file information
[gameInformation,unitInformation,otherGameplayInfo] = prp.parseReplayFile(sys.argv[1])


#GAME STATISTIC 1: % of games won by each team
#RETURNS a dictionary, the keys are the teamnames and the values are % won
def percentageOfGamesWon(gameInformation):
    gamesTeamAWon = 0
    totalNumberOfGames = 0
    for dictionary in gameInformation:
        if dictionary['winner']=='A':
            gamesTeamAWon += 1
        totalNumberOfGames += 1
    percentWonInfo = {}
    percentWonByA = float(gamesTeamAWon)/totalNumberOfGames
    percentWonInfo[gameInformation[0]['team-a']]= percentWonByA
    percentWonInfo[gameInformation[0]['team-b']]= 1 - percentWonByA
    return percentWonInfo

#GAME STATISTIC 2: amount of supply per team over time
#RETURNS a list of dictionaries. Each dictionary is a match
#keys are teamnames, the values are lists of supply
def supplyPerTeamOverTime(otherGameplayInfo):
    supplyOverTime = []
    for subList in otherGameplayInfo:
        supplyDictionary = {'A':[],'B':[],'ZOMBIE':[],'NEUTRAL':[]}
        for dictionary in subList:
            resources = dictionary['resources']
            for key in resources.keys():
                supplyDictionary[key].append(resources[key])
        supplyOverTime.append(supplyDictionary)
    return supplyOverTime

#GAME STATISTIC 3: types of units over time
def unitsOverTime(unitInformation, team):
    unitsOverTime = []
    for subList in unitInformation:
        if team == 'ZOMBIE':
            unitDictionary = {'STANDARDZOMBIE':[],'RANGEDZOMBIE':[],'FASTZOMBIE':[],'BIGZOMBIE':[]}
            unitCount = {'STANDARDZOMBIE':0,'RANGEDZOMBIE':0,'FASTZOMBIE':0,'BIGZOMBIE':0}
        else:
            unitDictionary = {'SOLDIER':[],'GUARD':[],'ARCHON':[],'SCOUT':[],'VIPER':[],'TURRET':[],'TTM':[]}
            unitCount = {'SOLDIER':0,'GUARD':0,'ARCHON':0,'SCOUT':0,'VIPER':0,'TURRET':0,'TTM':0}
        for aRound in subList:
            for ID in aRound:
                unitType = aRound[ID]['type']
                unitTeam = aRound[ID]['team'] 
                if unitTeam==team and unitType in unitDictionary.keys():
                    unitCount[unitType] += 1
            for key in unitDictionary:
                unitDictionary[key].append(unitCount[key])
                unitCount[key] = 0
        unitsOverTime.append(unitDictionary)
    return unitsOverTime
                    

teamAUnits = unitsOverTime(unitInformation,'A')
plt.plot(teamAUnits[0]['SOLDIER'])
plt.show()


gamesWon = percentageOfGamesWon(gameInformation)
colors = ['yellowgreen','gold']
labels = gamesWon.keys()
patches, texts, junk = plt.pie(gamesWon.values(),colors=colors,autopct='%1.1f%%')
plt.legend(patches,labels,loc='best')
plt.title('Games Won')
plt.show()
