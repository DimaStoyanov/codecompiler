var languageSelector = $('#language');
var codeSelector = $('#code');
var compileResponseSelector = $('#compile-response');
var compilePB = $('#compile-pb');
var compileBtn = $('#compile');

var submissionSelector = $('#submission-id');
var inputSelector = $('#stdin');
var tlSelector = $('#tl');
var mlSelector = $('#ml');
var runResponseSelector = $('#run-response');
var runPB = $('#run-pb');
var runBtn = $('#run');

var compileAndRun = $('#compileAndRun');
var contest = $('#contest');
var submission = $('#submission');

var compileAndRunContent = $('#compileAndRunContent');
var contestContent = $('#contestContent');
var submissionContent = $('#submissionContent');


compileAndRun.click(onTabClick(compileAndRun, compileAndRunContent));
contest.click(onTabClick(contest, contestContent));
submission.click(onTabClick(submission, submissionContent));


function onTabClick(linkSelector, contentSelector) {
    return function () {
        compileAndRun.removeClass("active");
        contest.removeClass("active");
        submission.removeClass("active");
        linkSelector.addClass("active");
        compileAndRunContent.addClass('d-none');
        contestContent.addClass("d-none");
        submissionContent.addClass("d-none");
        contentSelector.removeClass("d-none");
    }

}


compileBtn.click(function () {
    showPb(compilePB, contestResponse, compileBtn);
    var payload = {
        language: languageSelector.val(),
        sourceCode: codeSelector.val()
    };
    $.ajax({
        url: '/compile-submissions/',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        onDone(compilePB, compileResponseSelector, compileBtn, resp);
    }).fail(function (resp) {
        onDone(compilePB, compileResponseSelector, compileBtn, resp.responseJSON);
    })
});

runBtn.click(function () {
    showPb(runPB, runResponseSelector, runBtn);
    var payload = {
        submissionId: submissionSelector.val(),
        input: inputSelector.val(),
        memoryLimit: mlSelector.val(),
        executionTimeLimit: tlSelector.val()
    };
    $.ajax({
        url: '/run-submissions/',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        onDone(runPB, runResponseSelector, runBtn, resp);
    }).fail(function (resp) {
        onDone(runPB, runResponseSelector, runBtn, resp.responseJSON);

    })
});


var testList = $('.test-container');
var addTestBtn = $("#add_test");
var testNumber = 0;

var contestName = $('#name');
var contestML = $('#memoryLimit');
var contestTL = $('#timeLimit');
var saveTest = $('#save_test');

var contestResponse = $('#contest-response');
var contestPB = $('#contest-pb');

addTestBtn.click(function () {
    var testContent = $(document.createElement('div'));
    testContent.addClass("row");

    testNumber++;
    var number = $(document.createElement("div"));
    number.addClass("col-1 pt-5");
    number.text(testNumber);

    var input = createInput("Test input");
    input.addClass("col-5");

    var output = createInput("Expected test output");
    output.addClass("col-6");

    testContent.append(number);
    testContent.append(input);
    testContent.append(output);
    testList.append(testContent);
});

function createInput(label) {
    var inputDiv = $(document.createElement("div"));
    inputDiv.addClass("form-group");
    var inputLabel = $(document.createElement("label"));
    inputLabel.text(label);//<span style="color: red"> *</span>
    var requiredSpan = $(document.createElement("span"));
    requiredSpan.text("*");
    requiredSpan.css("color", "red");
    inputLabel.append(requiredSpan);
    var input = $(document.createElement("textarea"));
    input.addClass("form-control");


    inputLabel.append(input);
    inputDiv.append(inputLabel);
    return inputDiv;

}

saveTest.click(function () {
    showPb(contestPB, contestResponse, saveTest);
    var data = {
        memoryLimit: contestML.val(),
        timeLimit: contestTL.val(),
        name: contestName.val(),
        tests: collectTests()
    };
    $.ajax({
        url: '/contests/',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data)
    }).done(function (resp) {
        onDone(contestPB, contestResponse, saveTest, resp);
    }).fail(function (resp) {
        onDone(contestPB, contestResponse, saveTest, resp.responseJSON);
    })
});


function collectTests() {
    var inputs = $('.test-container textarea');
    var tests = [];

    for (var i = 0; i < inputs.length; i++) {
        var test = {
            input: inputs.get(i).value,
            expectedOutput: inputs.get(i + 1).value
        };
        tests.push(test);
        i++;
    }
    return tests;
}


var submissionLang = $('#submissionLang');
var submissionCode = $('#submissionCode');
var contestId = $('#contestId');
var saveSubmission = $('#createSubmission');

var submissioResponse = $('#submission-response');
var submissionPB = $('#submission-pb');


saveSubmission.click(function () {
    showPb(submissionPB, submissioResponse, saveSubmission);
    var data = {
        contestId: contestId.val(),
        submission: {
            language: submissionLang.val(),
            sourceCode: submissionCode.val()
        }
    };
    $.ajax({
        url: '/submissions/',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data)
    }).done(function (resp) {
        onDone(submissionPB, submissioResponse, saveSubmission, resp);
    }).fail(function (resp) {
        onDone(submissionPB, submissioResponse, saveSubmission, resp.responseJSON);
    })
});


var submissionGetId = $('#submissionGetId');
var withLang = $('#withLang');
var withSource = $('#withSource');
var autorefresh = $('#autorefresh');
var getSubmissionResultBtn = $('#getSubmissionResult');
var submissionResultPB = $('#submission-result-pb');
var submissionResultReponse = $('#submission-result-response');

var updTimer;

getSubmissionResultBtn.click(getSubmissionResult.bind(null, true));



function getSubmissionResult(forced) {
    if (autorefresh.prop('checked') && forced) {
        if (updTimer) {
            clearInterval(updTimer);
        }
        updTimer = setInterval(getSubmissionResult.bind(null, false), 500);
    }

    $.ajax({
        url: '/submissions/' + submissionGetId.val()
            + "?withLang=" + withLang.val()
            + "&withSource=" + withSource.val(),
        type: 'GET',
        contentType: "application/json; charset=utf-8"
    }).done(function (resp) {
        onDone(submissionResultPB, submissionResultReponse, getSubmissionResultBtn, resp);
        if (resp.status !== 'RUNNING_TEST' && resp.status !== 'WAITING_IN_QUEUE' && resp.status !== 'COMPILING') {
            clearInterval(updTimer);
        }
    }).fail(function (resp) {
        onDone(submissionResultPB, submissionResultReponse, getSubmissionResultBtn, resp.responseJSON);
    })
}

function showPb(pb, response, btn) {
    pb.show();
    response.text('');
    btn.attr('disabled', true)
}

function onDone(pb, response, btn, data) {
    pb.hide();
    btn.attr('disabled', false);
    response.text(prettyJSON(data));
}


function prettyJSON(data) {
    return JSON.stringify(data, null, 2);
}
