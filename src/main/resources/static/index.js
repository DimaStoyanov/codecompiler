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

compileBtn.click(function () {
    compilePB.show();
    compileResponseSelector.text('');
    disable(compileBtn);


    var payload = {
        language: languageSelector.val(),
        sourceCode: codeSelector.val()
    };
    $.ajax({
        url: '/compile-submissions/',
        type: 'POST',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        enable(compileBtn);
        compilePB.hide();
        compileResponseSelector.text(prettyJSON(resp));
    }).fail(function (resp) {
        enable(compileBtn);
        compilePB.hide();
        compileResponseSelector.text(prettyJSON(resp.responseJSON));
    })
});

runBtn.click(function () {
    runPB.show();
    runResponseSelector.text('');
    disable(runBtn);

    var payload = {
        submissionId: submissionSelector.val(),
        input: inputSelector.val(),
        memoryLimit: mlSelector.val(),
        executionTimeLimit: tlSelector.val()
    };
    $.ajax({
        url: '/submissions/',
        type: 'POST',
        contentType:"application/json; charset=utf-8",
        data: JSON.stringify(payload)
    }).done(function (resp) {
        enable(runBtn);
        runPB.hide();
        runResponseSelector.text(prettyJSON(resp));
    }).fail(function (resp) {
        enable(runBtn);
        runPB.hide();
        runResponseSelector.text(prettyJSON(resp.responseJSON));
    })
});


function prettyJSON(data) {
    return JSON.stringify(data, null, 2);
}


function disable(selector) {
    selector.attr('disabled', true)
}

function enable(selector) {
    selector.attr('disabled', false)
}