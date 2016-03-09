import xml.etree.ElementTree as ET
import sys
import matplotlib.pyplot as plt
import numpy as np

if len(sys.argv) != 2:
    exit

tree = ET.parse(str(sys.argv[1]))
root = tree.getroot()

#GAME STATISTIC 1: % OF GAMES WON BY TEAM A
gamesPlayed = 0
gamesWonByA = 0
gamesWonByB = 0

for child in root:
    if child.tag == 'ser.MatchFooter':
        if child.attrib['winner']=='A':
            gamesWonByA += 1
        else:
            gamesWonByB += 1
        gamesPlayed += 1

print(gamesWonByA)
print(gamesWonByB)
print(gamesPlayed)
colors = ['yellowgreen', 'gold']
labels = ['Team A','Team B']
patches, texts, junk = plt.pie([float(gamesWonByA),float(gamesWonByB)],colors=colors,autopct='%1.1f%%')
plt.legend(patches,labels,loc='best')
plt.title('Games Won')
plt.show()

#GAME STATISTIC 2: AMOUNT OF SUPPLY PER PLAYER OVER TIME
#teamAResources = []
#teamBResources = []
#teamASubList = []
#teamBSubList = []
#for child in root:
#    if child.tag=='ser.RoundDelta':
#        children = child.getchildren()
#        for grandChild in children:
#            if grandChild.tag == 'sig.TeamResourceSignal':
#                if grandChild.attrib['team']=='A':
#                    teamASubList.append(grandChild.attrib['resource'])
#                elif grandChild.attrib['team']=='B':
#                    teamBSubList.append(grandChild.attrib['resource'])
#    elif child.tag == 'ser.ExtensibleMetadata':
#        teama=child.attrib['team-a']
#        teamb=child.attrib['team-b']
#    elif child.tag == 'ser.MatchFooter':
#        teamAResources.append(teamASubList)
#        teamBResources.append(teamBSubList)
#        teamASubList = []
#        teamBSubList = []
#plt.plot(teamAResources[0])
#plt.plot(teamBResources[0])
#plt.legend(['%s'%teama, '%s'%teamb],loc='best')
#plt.xlabel('Rounds')
#plt.ylabel('Resources')
#plt.title('Resources Per Team Over Time')
#plt.show()

##GAME STATISTIC 3: DAMAGE TAKEN OVER TIME
#teamAarchonIDs = []
#teamBarchonIDs = []
#teamAarchonIDSubList = []
#teamBarchonIDSubList = []

#killedUnitIDs = []
#killedUnitIDSubList = []

#teamAspawnedSoldierIDs = []
#teamBspawnedSoldierIDs = []
#teamAspawnedSoldierIDsSubList = []
#teamBspawnedSoldierIDsSubList = []

#teamAspawnedGuardsIDs = []
#teamBspawnedGuardsIDs = []
#teamAspawnedGuardsIDsSubList = []
#teamBspawnedGuardsIDsSubList = []

#teamAspawnedTurretsIDs = []
#teamBspawnedTurretsIDs = []
#teamAspawnedTurretsIDsSubList = []
#teamBspawnedTurretsIDsSubList = []

#SroundNumSubListA = []
#SroundNumA = []
#SroundNumSubListB = []
#SroundNumB = []

#GroundNumSubListA = []
#GroundNumA = []
#GroundNumSubListB = []
#GroundNumB = []

#TroundNumSubListA = []
#TroundNumA = []
#TroundNumSubListB = []
#TroundNumB = []

#for child in root:
#    if child.tag=='ser.RoundDelta':
#        SroundNumSubListB.append(len(list(set(teamBspawnedSoldierIDsSubList)-set(killedUnitIDSubList))))
#        SroundNumSubListA.append(len(list(set(teamAspawnedSoldierIDsSubList)-set(killedUnitIDSubList))))
#        GroundNumSubListB.append(len(list(set(teamAspawnedGuardsIDsSubList)-set(killedUnitIDSubList))))
#        GroundNumSubListA.append(len(list(set(teamBspawnedGuardsIDsSubList)-set(killedUnitIDSubList))))
#        TroundNumSubListA.append(len(list(set(teamAspawnedTurretsIDsSubList)-set(killedUnitIDSubList))))
#        TroundNumSubListB.append(len(list(set(teamAspawnedTurretsIDsSubList)-set(killedUnitIDSubList))))
#        children = child.getchildren()
#        for grandChild in children:
#            if grandChild.tag == 'sig.SpawnSignal':
#                if grandChild.attrib['team']=='A' and grandChild.attrib['type']=='ARCHON':
#                    teamAarchonIDSubList.append(grandChild.attrib['robotID'])
#                elif grandChild.attrib['team']=='B' and grandChild.attrib['type']=='ARCHON':
#                    teamBarchonIDSubList.append(grandChild.attrib['robotID'])
#                elif grandChild.attrib['team']=='A' and grandChild.attrib['type']=='SOLDIER':
#                    teamAspawnedSoldierIDsSubList.append(grandChild.attrib['robotID'])
#                elif grandChild.attrib['team']=='B' and grandChild.attrib['type']=='SOLDIER':
#                    teamBspawnedSoldierIDsSubList.append(grandChild.attrib['robotID'])
#                elif grandChild.attrib['team']=='A' and grandChild.attrib['type']=='GUARD':
#                    teamAspawnedGuardsIDsSubList.append(grandChild.attrib['robotID'])
#                elif grandChild.attrib['team']=='B' and grandChild.attrib['type']=='GUARD':
#                    teamBspawnedGuardsIDsSubList.append(grandChild.attrib['robotID'])
#                elif grandChild.attrib['team']=='A' and grandChild.attrib['type']=='TURRET':
#                    teamAspawnedTurretsIDsSubList.append(grandChild.attrib['robotID'])
#                elif grandChild.attrib['team']=='B' and grandChild.attrib['type']=='TURRET':
#                    teamBspawnedTurretsIDsSubList.append(grandChild.attrib['robotID'])
#            elif grandChild.tag == 'sig.DeathSignal':
#                killedUnitIDSubList.append(grandChild.attrib['objectID'])
#    elif child.tag=='ser.ExtensibleMetadata':
#        teama=child.attrib['team-a']
#        teamb=child.attrib['team-b']
#    elif child.tag == 'ser.MatchFooter':
#        teamAarchonIDs.append(teamAarchonIDSubList)
#        teamBarchonIDs.append(teamBarchonIDSubList)
#        killedUnitIDs.append(killedUnitIDSubList)
#        teamAspawnedSoldierIDs.append(teamAspawnedSoldierIDsSubList)
#        teamBspawnedSoldierIDs.append(teamBspawnedSoldierIDsSubList)
#        teamAspawnedGuardsIDs.append(teamAspawnedGuardsIDsSubList)
#        teamBspawnedGuardsIDs.append(teamBspawnedGuardsIDsSubList)
#        teamAspawnedTurretsIDs.append(teamAspawnedTurretsIDsSubList)
#        teamBspawnedTurretsIDs.append(teamBspawnedTurretsIDsSubList)
#        SroundNumA.append(SroundNumSubListA)
#        SroundNumSubListA = []
#        SroundNumB.append(SroundNumSubListB)
#        SroundNumSubListB = []
#        GroundNumA.append(GroundNumSubListA)
#        GroundNumSubListA = []
#        GroundNumB.append(GroundNumSubListB)
#        GroundNumSubListB = []
#        TroundNumA.append(TroundNumSubListA)
#        TroundNumSubListA = []
#        TroundNumB.append(TroundNumSubListB)
#        TroundNumSubListB = []
#        teamAarchonIDSubList = []
#        teamBarchonIDSubList = []
#        killedUnitIDSubList = []
#        teamAspawnedSoldierIDsSubList = []
#        teamBspawnedSoldierIDsSubList = []
#        teamAspawnedGuardsIDsSubList = []
#        teamBspawnedGuardsIDsSubList = []
#        teamAspawnedTurretsIDsSubList = []
#        teamBspawnedTurretsIDsSubList = []

#plt.plot(SroundNumA[0])
#plt.plot(SroundNumB[0])
#plt.plot(GroundNumA[0])
#plt.plot(GroundNumB[0])
#plt.plot(TroundNumA[0])
#plt.plot(TroundNumB[0])
#colors = ['#000000','#000000','#000000','#000000','#FFFFFF','#FFFFFF']
#plt.legend(['%s'%teama + " soldier", '%s'%teamb + " soldier", '%s'%teama + " guard", '%s'%teamb + " guard", '%s'%teama + " turret", '%s'%teamb + " turret"],loc='best',colors=colors)

#plt.rc('axes', prop_cycle=(cycler('color', ['r', 'g', 'b', 'y']) +
#                           cycler('linestyle', ['-', '--', ':', '-.'])))

#plt.show()
#print("Team A")
#print(teamAarchonIDs[0])
#print("Team B")
#print(teamBarchonIDs[0])
#print()
#print("killed units")
#print(len(killedUnitIDs[0]))
#
#print("Archons in A")
#print(len(set(teamAarchonIDs[0]).intersection(killedUnitIDs[0])))
#print("Archons in B")
#print(len(set(teamBarchonIDs[0]).intersection(killedUnitIDs[0])))
#
#print("Soldiers Spawned for team A")
#print(len(teamAspawnedSoldierIDs[0]))
#print("Soldiers Spawned for team B")
#print(len(teamBspawnedSoldierIDs[0]))
#
#print("Guards Spawned for team A")
#print(len(teamAspawnedGuardsIDs[0]))
#print("Guards Spawned for team B")
#print(len(teamBspawnedGuardsIDs[0]))
#
#print("Turrets Spawned for team A")
#print(len(teamAspawnedTurretsIDs[0]))
#print("Turrets Spawned for team B")
#print(len(teamBspawnedTurretsIDs[0]))


#x = np.arange(0, 3000, 1);
#y = roundNum[0]
#plt.plot(roundNumA[0])
#plt.plot(roundNumB[0])
#plt.show()

#plt.plot(teamAspawnedSoldierIDs[0])
#plt.plot(teamBspawnedSoldierIDs[0])
#plt.plot(teamAspawnedGuardsIDs[0])
#plt.plot(teamBspawnedGuardsIDs[0])
#plt.plot(teamAspawnedTurretsIDs[0])
#plt.plot(teamBspawnedTurretsIDs[0])
#plt.plot(roundNumA[0])
#plt.plot(roundNumB[0])
#plt.legend(['%s'%teama, '%s'%teamb],loc='best')
#plt.xlabel('Units')
#plt.ylabel('Rounds')
#plt.title('Units Per Team Over Time')
#plt.show()
#
#
#plt.figure()
#plt.hist([roundNumA[0],roundNumB[0]], 15, stacked=True, normed = True)
#plt.show()

#for x in range(0,counter):
#plt.plot(teamAarchonIDs[0])
#plt.plot(teamBarchonIDs[0])
#plt.legend(['%s'%teama, '%s'%teamb],loc='best')
#plt.xlabel('Rounds')
#plt.ylabel('Resources')
#plt.title('Resources Per Team Over Time')
#plt.show()

#GAME STATISTIC 4: CUMMULATIVE UNITS DESTROYED
