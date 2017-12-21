const R = require('ramda')
const fs = require('fs')
const path = require('path')

const lines = fs.readFileSync(path.resolve(__dirname, process.argv[2])).toString().split('\n')
const TOTAL_PROCESSES = parseInt(process.argv[3], 10)

const ROUND_INDEX = 1
const PHASE_INDEX = 2
const PID_INDEX = 3
const VALUE_INDEX = 4

const notificationTable = {}
const proposalTable = {}

lines.forEach((l) => {
    const matches = (/^(\d+)(N|P)\s(\d+)\s+(\d)+/gi).exec(l)

    if (matches) {
        if (matches[PHASE_INDEX] === 'N') {
            if (!notificationTable[matches[ROUND_INDEX]]) {
                notificationTable[matches[ROUND_INDEX]] = {}
            }

            notificationTable[matches[ROUND_INDEX]][matches[PID_INDEX]] = matches[VALUE_INDEX]
        }

        if (matches[PHASE_INDEX] === 'P') {
            if (!proposalTable[matches[ROUND_INDEX]]) {
                proposalTable[matches[ROUND_INDEX]] = {}
            }

            proposalTable[matches[ROUND_INDEX]][matches[PID_INDEX]] = matches[VALUE_INDEX]
        }
    }
})

const processIds = R.range(1, TOTAL_PROCESSES + 1).map((pid) => pid.toString())


R.keys(notificationTable).forEach((r) => {
    const strN = [`${r}N`]
    processIds.forEach((pid) => {
        strN.push(notificationTable[r][pid] ? notificationTable[r][pid] : ' ')
    })
    console.log(strN.join(' & ') + ' \\\\')

    const strP = [`${r}P`]
    processIds.forEach((pid) => {
        if (proposalTable[r]) {
            strP.push(proposalTable[r][pid] ? proposalTable[r][pid] : ' ')
        } else {
            strP.push(' ')
        }
    })
    console.log(strP.join(' & ') + ' \\\\')
    console.log('\\hline')
})
