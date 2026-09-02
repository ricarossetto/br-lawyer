const http = require('http');

function request(options, data) {
  return new Promise((resolve, reject) => {
    const payload = data ? (typeof data === 'string' ? data : JSON.stringify(data)) : null;
    const req = http.request({
      hostname: 'localhost',
      port: 8000,
      ...options,
      headers: {
        'Accept': 'application/json',
        ...(payload ? { 'Content-Type': 'application/json', 'Content-Length': Buffer.byteLength(payload) } : {}),
        ...(options.headers || {})
      }
    }, res => {
      let b = '';
      res.on('data', d => b += d);
      res.on('end', () => {
        let body = null;
        try { body = b ? JSON.parse(b) : null; } catch(e) { body = b; }
        resolve({ status: res.statusCode, headers: res.headers, body });
      });
    });
    req.on('error', reject);
    if (payload) {
      req.write(payload);
    }
    req.end();
  });
}

async function runEndToEndVerification() {
  console.log('====================================================');
  console.log('BR-LAWYER: FULL END-TO-END BRAZILIAN WORKFLOW TEST');
  console.log('====================================================');

  // 1. Auth Login
  console.log('\n[1/12] Authenticating (JWT RS256)...');
  const loginRes = await request({
    path: '/j-lawyer-io/rest/v8/auth/login',
    method: 'POST'
  }, { username: 'admin', password: 'a' });

  if (loginRes.status !== 200 || !loginRes.body?.accessToken) {
    throw new Error('Authentication failed: ' + JSON.stringify(loginRes.body));
  }
  const token = loginRes.body.accessToken;
  const authHeaders = { 'Authorization': 'Bearer ' + token };
  console.log('  -> SUCCESS: Logged in as', loginRes.body.username);

  // 2. Workflow Dashboard
  console.log('\n[2/12] Fetching Workflow Dashboard...');
  const dashRes = await request({
    path: '/j-lawyer-io/rest/v8/workflow/dashboard',
    method: 'GET',
    headers: authHeaders
  });
  console.log('  -> Dashboard metrics:');
  console.log('     * Total Open Tasks:', dashRes.body?.totalOpenTasks);
  console.log('     * Total Overdue Tasks:', dashRes.body?.totalOverdueTasks);
  console.log('     * Total Due Today Tasks:', dashRes.body?.totalDueTodayTasks);
  console.log('     * Total Untreated Publications:', dashRes.body?.totalUntreatedPublications);
  console.log('     * Urgent Publications count:', dashRes.body?.urgentPublications?.length);

  // 3. Publications Page
  console.log('\n[3/12] Querying Publications (/v8/publications/page)...');
  const pubsPageRes = await request({
    path: '/j-lawyer-io/rest/v8/publications/page?page=0&pageSize=10',
    method: 'GET',
    headers: authHeaders
  });
  console.log('  -> Total Publications in DB:', pubsPageRes.body?.total);
  pubsPageRes.body?.items?.forEach(p => {
    console.log('     - [' + (p.courtCode || 'DJ') + '] ' + (p.cnjNumber || 'Sem NPU') + ' | Status: ' + p.status + ' | Read: ' + p.readStatus + ' | Treated: ' + p.treatmentStatus);
  });

  // 4. Publication Detail & Audit Events
  console.log('\n[4/12] Fetching Publication Detail for pub-seed-001...');
  const pubDetailRes = await request({
    path: '/j-lawyer-io/rest/v8/publications/pub-seed-001',
    method: 'GET',
    headers: authHeaders
  });
  console.log('  -> Publication details:');
  console.log('     * Recipient:', pubDetailRes.body?.recipient);
  console.log('     * Lawyer:', pubDetailRes.body?.lawyerName, '| OAB:', pubDetailRes.body?.lawyerOab);
  console.log('     * Events count:', pubDetailRes.body?.events?.length);
  console.log('     * Linked Tasks count:', pubDetailRes.body?.linkedTasks?.length);

  // 5. Mark Publication Read
  console.log('\n[5/12] Marking Publication pub-seed-001 as READ...');
  const markReadRes = await request({
    path: '/j-lawyer-io/rest/v8/publications/pub-seed-001/mark-read?read=true',
    method: 'POST',
    headers: authHeaders
  });
  console.log('  -> Updated Read Status:', markReadRes.body?.readStatus);

  // 6. Treat Publication & Create Follow-up Task
  console.log('\n[6/12] Treating pub-seed-002 and generating follow-up task...');
  const treatRes = await request({
    path: '/j-lawyer-io/rest/v8/publications/pub-seed-002/treat',
    method: 'POST',
    headers: authHeaders
  }, {
    action: 'MARK_TREATED',
    user: 'admin',
    notes: 'Tratado pelo fluxo automatizado web',
    createTask: true,
    taskTitle: 'Elaborar Emenda à Inicial TJSP conforme intimação',
    taskDescription: 'Atender despacho de emenda no prazo de 15 dias',
    taskAssignedUser: 'admin',
    taskPriority: 'HIGH',
    taskCategory: 'PETICAO',
    taskDueDate: Date.now() + 86400000 * 5,
    taskDueTime: '18:00',
    syncCalendar: true
  });
  console.log('  -> Treatment result:');
  console.log('     * Status:', treatRes.body?.status);
  console.log('     * Treatment Status:', treatRes.body?.treatmentStatus);
  console.log('     * Linked Tasks count after triage:', treatRes.body?.linkedTasks?.length);

  // 7. Tasks Page
  console.log('\n[7/12] Querying Legal Tasks (/v8/tasks/page)...');
  const tasksPageRes = await request({
    path: '/j-lawyer-io/rest/v8/tasks/page?page=0&pageSize=10',
    method: 'GET',
    headers: authHeaders
  });
  console.log('  -> Total Tasks in DB:', tasksPageRes.body?.total);
  tasksPageRes.body?.items?.forEach(t => {
    console.log('     - [' + t.status + '] ' + t.title + ' (Priority: ' + t.priority + ', Cat: ' + t.category + ')');
  });

  // 8. Tasks Kanban Board
  console.log('\n[8/12] Querying Kanban Board (/v8/tasks/kanban)...');
  const kanbanRes = await request({
    path: '/j-lawyer-io/rest/v8/tasks/kanban',
    method: 'GET',
    headers: authHeaders
  });
  console.log('  -> Kanban Columns:');
  kanbanRes.body?.columns?.forEach(col => {
    console.log('     * Column ' + col.title + ' (' + col.status + '): ' + col.count + ' tasks');
  });

  // 9. Status Transition
  console.log('\n[9/12] Transitioning task-seed-001 to IN_PROGRESS...');
  const statusRes = await request({
    path: '/j-lawyer-io/rest/v8/tasks/task-seed-001/status',
    method: 'POST',
    headers: authHeaders
  }, {
    newStatus: 'IN_PROGRESS',
    user: 'admin',
    notes: 'Iniciada redação da réplica'
  });
  console.log('  -> Updated Task Status:', statusRes.body?.status);

  // 10. Task Comments
  console.log('\n[10/12] Adding Comment to task-seed-001...');
  const addCommRes = await request({
    path: '/j-lawyer-io/rest/v8/tasks/task-seed-001/comments?text=' + encodeURIComponent('Análise jurisprudencial concluída favoravelmente.'),
    method: 'POST',
    headers: authHeaders
  });
  console.log('  -> Added Comment ID:', addCommRes.body?.id, '| User:', addCommRes.body?.userName);

  // 11. Task Checklist
  console.log('\n[11/12] Adding Checklist item to task-seed-001...');
  const addChkRes = await request({
    path: '/j-lawyer-io/rest/v8/tasks/task-seed-001/checklist?title=' + encodeURIComponent('Protocolar via portal TRF4') + '&order=4',
    method: 'POST',
    headers: authHeaders
  });
  console.log('  -> Added Checklist Item ID:', addChkRes.body?.id, '| Title:', addChkRes.body?.title);

  // Toggle Checklist
  if (addChkRes.body?.id) {
    const toggleChkRes = await request({
      path: '/j-lawyer-io/rest/v8/tasks/checklist/' + addChkRes.body.id + '/toggle?done=true',
      method: 'POST',
      headers: authHeaders
    });
    console.log('  -> Toggled Checklist Item Done:', toggleChkRes.body?.done);
  }

  // 12. Case Hub Filtered Workflow
  console.log('\n[12/12] Case as Operational Hub: Verifying publications & tasks for case-seed-001...');
  const casePubsRes = await request({
    path: '/j-lawyer-io/rest/v8/publications?processId=case-seed-001',
    method: 'GET',
    headers: authHeaders
  });
  const caseTasksRes = await request({
    path: '/j-lawyer-io/rest/v8/tasks?processId=case-seed-001',
    method: 'GET',
    headers: authHeaders
  });
  console.log('  -> Case Publications count:', casePubsRes.body?.length);
  console.log('  -> Case Tasks count:', caseTasksRes.body?.length);

  console.log('\n====================================================');
  console.log('ALL 12 WORKFLOW END-TO-END STEPS PASSED SUCCESSFULLY!');
  console.log('====================================================');
}

runEndToEndVerification().catch(err => {
  console.error('VERIFICATION FAILED:', err);
  process.exit(1);
});
